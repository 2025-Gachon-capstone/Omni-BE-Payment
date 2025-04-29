package org.example.omnibepayment.converter;

import org.example.omnibepayment.dto.OrderItemResDto;
import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.dto.ProductResDto;
import org.example.omnibepayment.dto.TossResDto;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.Payment;
import org.example.omnibepayment.entity.type.PaymentStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public static PaymentResDto.GetPaymentPage toGetPaymentPage(Page<Payment> paymentPage, Map<Long, ProductResDto.GetProductList> productMap) {

        List<PaymentResDto.GetPayment> getPayments = paymentPage.stream()
                .map(payment -> toGetPayment(payment, productMap))
                .collect(Collectors.toList());

        return PaymentResDto.GetPaymentPage.builder()
                .payments(getPayments)
                .isFirst(paymentPage.isFirst())
                .isLast(paymentPage.isLast())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .build();
    }

    public static PaymentResDto.GetPayment toGetPayment(Payment payment, Map<Long, ProductResDto.GetProductList> productMap) {
        Order order = payment.getOrder();
        List<OrderItemResDto.GetOrderItem> orderItemDtos = order.getOrderItems().stream()
                .map(orderItem -> OrderItemConverter.toGetOrderItem(orderItem, productMap.get(orderItem.getProductId())))
                .collect(Collectors.toList());

        return PaymentResDto.GetPayment.builder()
                .paymentId(payment.getPaymentId())
                .orderCode(order.getOrderCode())
                .createdAt(payment.getCreatedAt().format(FORMATTER))
                .orderName(order.getOrderName())
                .paymentPrice(payment.getPaymentAmount())
                .paymentStatus(payment.getStatus().name())
                .orderItems(orderItemDtos)
                .build();
    }

}
