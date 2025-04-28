package org.example.omnibepayment.service;

import lombok.extern.slf4j.Slf4j;
import org.example.omnibepayment.client.TossPaymentsClient;
import org.example.omnibepayment.common.apiPayload.code.status.ErrorStatus;
import org.example.omnibepayment.common.apiPayload.exception.GeneralException;
import org.example.omnibepayment.converter.PaymentConverter;
import org.example.omnibepayment.dto.PaymentReqDto;
import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.dto.TossReqDto;
import org.example.omnibepayment.dto.TossResDto;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.Payment;
import org.example.omnibepayment.entity.type.OrderStatus;
import org.example.omnibepayment.repository.OrderRepository;
import org.example.omnibepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final OrderService orderService;


    public PaymentServiceImpl(OrderRepository orderRepository,PaymentRepository paymentRepository,
                              TossPaymentsClient tossPaymentsClient, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.tossPaymentsClient = tossPaymentsClient;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public PaymentResDto.confirmResponse confirmPayment(PaymentReqDto.ConfirmRequest confirmRequest) {

        Order order = orderRepository.findByOrderCode(confirmRequest.getOrderCode())
                .orElseThrow(()-> new GeneralException(ErrorStatus._NOT_FOUND_ORDER));

        if (order.getOrderPrice().compareTo(BigDecimal.valueOf(confirmRequest.getTotalPrice())) != 0) {
            orderService.updateOrderStatusToDeny(order);
            throw new GeneralException(ErrorStatus._NOT_MATCH_PAYMENT_AMOUNT);
        }

        TossResDto.ConfirmResponse tossConfirmResponse;

        try{
            tossConfirmResponse = tossPaymentsClient.confirmPayment(
                    new TossReqDto.ConfirmRequest(
                        confirmRequest.getOrderCode(),
                        confirmRequest.getTotalPrice(),
                        confirmRequest.getPaymentKey()
                    )
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            orderService.updateOrderStatusToDeny(order);
            throw new GeneralException(ErrorStatus._TOSS_CONFIRM_FAIL);
        }

        if (!"DONE".equals(tossConfirmResponse.getStatus())) {
            orderService.updateOrderStatusToDeny(order);
            throw new GeneralException(ErrorStatus._TOSS_PAYMENT_NOT_DONE);
        }

        Payment savedPayment;

        try{
            savedPayment = paymentRepository.save(PaymentConverter.toPayment(order,tossConfirmResponse));

            orderRepository.findByMemberIdAndStatus(order.getMemberId(), OrderStatus.TRAIN)
                    .ifPresent(trainOrder -> {
                        trainOrder.setStatus(OrderStatus.PRIOR);
                        orderRepository.save(trainOrder);
                    });

            order.setPayment(savedPayment);
            order.setStatus(OrderStatus.TRAIN);
            orderRepository.save(order);


        }catch (Exception e) {

            try{
                tossPaymentsClient.cancelPayment(tossConfirmResponse.getPaymentKey(),
                        new TossReqDto.CancelRequest("서버 결제 저장 실패로 인한 자동 취소"));
            }catch (Exception e1) {
                log.error("결제 취소 요청 실패", e1);
            }

            try {
                orderService.updateOrderStatusToDeny(order);
            } catch (Exception denyException) {
                log.error("order deny 업데이트 실패", denyException);
            }

            throw new GeneralException(ErrorStatus._PAYMENT_SAVE_FAIL);
        }

        // todo 플라스크에 orderId 전달하기

        return PaymentConverter.toPaymentResDto(savedPayment);
    }

}
