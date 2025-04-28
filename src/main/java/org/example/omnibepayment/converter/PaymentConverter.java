package org.example.omnibepayment.converter;

import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.dto.TossResDto;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.Payment;
import org.example.omnibepayment.entity.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentConverter {

    public static Payment toPayment(Order order, TossResDto.ConfirmResponse confirmResponse){

        return Payment.builder()
                .order(order)
                .paymentKey(confirmResponse.getPaymentKey())
                .paymentAmount(BigDecimal.valueOf(confirmResponse.getTotalAmount()))
                .approvedAt(OffsetDateTime.parse(confirmResponse.getApprovedAt()).toLocalDateTime())
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    public static PaymentResDto.confirmResponse toPaymentResDto(Payment payment){

        return PaymentResDto.confirmResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderCode(payment.getOrder().getOrderCode())
                .paymentStatus(String.valueOf(payment.getStatus()))
                .build();
    }

}
