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

        // 1. 카드 서버에서 memberId 조회
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

        // 2. 주문 코드 생성 + 주문 메타데이터 계산
        String orderCode = UUID.randomUUID().toString();
        OrderMetadata metadata = calculateOrderMetadata(memberIdDto.getMemberId());

        // 3. 주문 저장
        Order savedOrder = orderRepository.save(OrderConverter.toOrder(createOrderDto, memberIdDto.getMemberId(),orderCode,metadata));

        // 4. 요청 상품 ID 추출
        List<Long> productIds = createOrderDto.getItems().stream()
                .map(OrderReqDto.CreateOrder.Item::getProductId)
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        // 5. 상품 정보 sponsor 서비스에서 조회

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

        // 6. 과거 주문 여부 확인 (reordered)
        Set<Long> previouslyOrderedProductIds = orderItemRepository
                .findByOrder_MemberIdAndProductIdIn(memberIdDto.getMemberId(), productIds)
                .stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());

        // 7. 주문 요청에서 수량, 담은 순서 정보 추출
        Map<Long, Integer> quantityMap = createOrderDto.getItems().stream()
                .collect(Collectors.toMap(OrderReqDto.CreateOrder.Item::getProductId, OrderReqDto.CreateOrder.Item::getQuantity));

        Map<Long, Integer> addToCartOrderMap = createOrderDto.getItems().stream()
                .collect(Collectors.toMap(OrderReqDto.CreateOrder.Item::getProductId, OrderReqDto.CreateOrder.Item::getAddToCartOrder));

        // 8. 주문 항목 생성
        List<OrderItem> orderItems = products.stream()
                .map(product -> OrderItem.builder()
                        .order(savedOrder)
                        .productId(product.getProductId())
                        .quantity(quantityMap.get(product.getProductId()))
                        .addToCartOrder(addToCartOrderMap.get(product.getProductId()))
                        .reordered(previouslyOrderedProductIds.contains(product.getProductId()))
                        .build())
                .collect(Collectors.toList());

        for (OrderItem orderItem : orderItems) {
            savedOrder.getOrderItems().add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

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
        LocalDateTime now = LocalDateTime.now();

        Order lastOrder = orderRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElse(null);

        long daysSincePrior = (lastOrder != null)
                ? ChronoUnit.DAYS.between(lastOrder.getCreatedAt(), now)
                : 0;

        Long orderCount = orderRepository.countByMemberId(memberId) + 1;
        int orderDow = now.getDayOfWeek().getValue() % 7;
        int orderHour = now.getHour();

        return new OrderMetadata(orderCount, orderDow, orderHour, daysSincePrior);
    }

}
