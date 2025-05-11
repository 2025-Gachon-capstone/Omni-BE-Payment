package org.example.omnibepayment.service;

import lombok.extern.slf4j.Slf4j;
import org.example.omnibepayment.client.CardClient;
import org.example.omnibepayment.client.SponsorClient;
import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.common.apiPayload.code.status.ErrorStatus;
import org.example.omnibepayment.common.apiPayload.exception.GeneralException;
import org.example.omnibepayment.converter.OrderConverter;
import org.example.omnibepayment.dto.*;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.OrderItem;
import org.example.omnibepayment.entity.type.OrderStatus;
import org.example.omnibepayment.repository.OrderItemRepository;
import org.example.omnibepayment.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CardClient cardClient;
    private final SponsorClient sponsorClient;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CardClient cardClient,
                            SponsorClient sponsorClient, OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.cardClient = cardClient;
        this.sponsorClient = sponsorClient;
        this.orderItemRepository = orderItemRepository;

    }

    @Override
    @Transactional
    public OrderResDto.CreateOrder createOrder(OrderReqDto.CreateOrder createOrderDto) {

        StopWatch stopWatch = new StopWatch("createOrder");

        // 1. 카드 서버에서 memberId 조회
        stopWatch.start("카드 서버 조회");
        CardReqDto.GetMemberId getMemberIdDto = new CardReqDto.GetMemberId(createOrderDto.getCardNumber());
        ApiResult<CardResDto.GetMemberId> response;
        CardResDto.GetMemberId memberIdDto;
        try {
            response = cardClient.getMemberId(getMemberIdDto);
            memberIdDto = response.getResult();
        } catch (Exception e) {
            log.error("Feign 통신 오류: 카드 서버 호출 실패", e);
            throw new GeneralException(ErrorStatus._CARD_SERVICE_ERROR);
        }

        if (response == null || !response.getIsSuccess() || response.getResult() == null) {
            throw new GeneralException(ErrorStatus._NOT_FOUND_CARD);
        }
        stopWatch.stop();

        // 2. 주문 코드 생성 + 주문 메타데이터 계산
        stopWatch.start("주문 메타데이터 계산");
        String orderCode = UUID.randomUUID().toString();
        OrderMetadata metadata = calculateOrderMetadata(memberIdDto.getMemberId());
        stopWatch.stop();

        // 3. 주문 저장
        stopWatch.start("주문 저장");
        Order savedOrder = orderRepository.save(OrderConverter.toOrder(createOrderDto, memberIdDto.getMemberId(),orderCode,metadata));
        stopWatch.stop();

        // 4. 요청 상품 ID 추출
        stopWatch.start("상품 ID 추출");
        List<Long> productIds = createOrderDto.getItems().stream()
                .map(OrderReqDto.CreateOrder.Item::getProductId)
                .distinct() // 중복 제거
                .collect(Collectors.toList());
        stopWatch.stop();

        // 5. 상품 정보 sponsor 서비스에서 조회
        stopWatch.start("스폰서 서버 상품 조회");
        ProductReqDto.GetProductList request = ProductReqDto.GetProductList.builder()
                .productIds(productIds)
                .build();

        ApiResult<List<ProductResDto.GetProductList>> productResponse;


        try {
            productResponse = sponsorClient.getProductList(request);
        } catch (Exception e) {
            log.error("Feign 통신 오류: 스폰서 서버 호출 실패", e);
            throw new GeneralException(ErrorStatus._SPONSOR_SERVICE_ERROR);
        }

        if (productResponse == null || !productResponse.getIsSuccess() || productResponse.getResult() == null) {
            throw new GeneralException(ErrorStatus._NOT_FOUND_PRODUCT);
        }

        List<ProductResDto.GetProductList> products = productResponse.getResult();

        // 상품 요청 확인
        if (products.size() != productIds.size()) {
            log.warn("요청한 상품 수: {}, 조회된 상품 수: {}", productIds.size(), products.size());
            throw new GeneralException(ErrorStatus._INVALID_PRODUCT_REQUEST);
        }
        stopWatch.stop();

        // 6. 과거 주문 여부 확인 (reordered)
        stopWatch.start("과거 주문 여부 조회");
        Set<Long> previouslyOrderedProductIds = orderItemRepository
                .findByOrder_MemberIdAndProductIdIn(memberIdDto.getMemberId(), productIds)
                .stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
        stopWatch.stop();

        // 7. 주문 요청에서 수량, 담은 순서 정보 추출
        stopWatch.start("수량/장바구니 순서 매핑");
        Map<Long, Integer> quantityMap = createOrderDto.getItems().stream()
                .collect(Collectors.toMap(OrderReqDto.CreateOrder.Item::getProductId, OrderReqDto.CreateOrder.Item::getQuantity));

        Map<Long, Integer> addToCartOrderMap = createOrderDto.getItems().stream()
                .collect(Collectors.toMap(OrderReqDto.CreateOrder.Item::getProductId, OrderReqDto.CreateOrder.Item::getAddToCartOrder));
        stopWatch.stop();

        // 8. 주문 항목 생성
        stopWatch.start("OrderItem 리스트 생성");
        List<OrderItem> orderItems = products.stream()
                .map(product -> OrderItem.builder()
                        .order(savedOrder)
                        .productId(product.getProductId())
                        .quantity(quantityMap.get(product.getProductId()))
                        .addToCartOrder(addToCartOrderMap.get(product.getProductId()))
                        .reordered(previouslyOrderedProductIds.contains(product.getProductId()))
                        .build())
                .collect(Collectors.toList());
        stopWatch.stop();

        for (OrderItem orderItem : orderItems) {
            savedOrder.getOrderItems().add(orderItem);
        }
        stopWatch.start("주문 항목 저장");
        orderItemRepository.saveAll(orderItems);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return OrderConverter.toCreateOrder(savedOrder);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrderStatusToDeny(Order order) {
        Order freshOrder = orderRepository.findById(order.getOrderId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_ORDER));

        freshOrder.setStatus(OrderStatus.DENY);

        orderRepository.saveAndFlush(freshOrder);
    }

    private OrderMetadata calculateOrderMetadata(Long memberId) {
        StopWatch stopWatch = new StopWatch("calculateOrderMetadata");

        stopWatch.start("가장 최근 주문 조회");
        LocalDateTime now = LocalDateTime.now();

        Order lastOrder = orderRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElse(null);
        stopWatch.stop();

        stopWatch.start("daysSincePrior 계산");
        long daysSincePrior = (lastOrder != null)
                ? ChronoUnit.DAYS.between(lastOrder.getCreatedAt(), now)
                : 0;
        stopWatch.stop();

        stopWatch.start("총 주문 수 계산");
        Long orderCount = orderRepository.countByMemberId(memberId) + 1;
        stopWatch.stop();

        stopWatch.start("요일/시간 계산");
        int orderDow = now.getDayOfWeek().getValue() % 7;
        int orderHour = now.getHour();
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return new OrderMetadata(orderCount, orderDow, orderHour, daysSincePrior);
    }

    @Transactional
    @Override
    public void expireOldPendingOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(OrderStatus.PENDING, threshold);

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.DENY);
        }

        log.info("30분 이상 PENDING 상태였던 주문 {}건을 DENY로 변경", expiredOrders.size());
    }

}
