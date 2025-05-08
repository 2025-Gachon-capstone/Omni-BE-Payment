package org.example.omnibepayment.converter;

import org.example.omnibepayment.dto.*;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.Payment;
import org.example.omnibepayment.entity.type.PaymentStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                .pageSize(paymentPage.getTotalPages())
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

    public static Map<Long, String> toProductSponsorNameMap(List<ProductResDto.GetProductList> products) {
        return products.stream()
                .collect(Collectors.toMap(
                        ProductResDto.GetProductList::getProductId,
                        ProductResDto.GetProductList::getSponsorName
                ));
    }

    public static PaymentResDto.GetPaymentForAdmin convert(Payment payment, MemberResDto.GetMemberByLoginId member, List<String> sponsorNames) {
        return PaymentResDto.GetPaymentForAdmin.builder()
                .paymentId(payment.getPaymentId())
                .loginId(member.getLoginId())
                .memberName(member.getMemberName())
                .createAt(payment.getCreatedAt().format(FORMATTER))
                .orderCode(payment.getOrder().getOrderCode())
                .orderName(payment.getOrder().getOrderName())
                .sponsorName(sponsorNames)
                .totalPrice(payment.getPaymentAmount())
                .build();
    }

    public static PaymentResDto.GetPaymentForAdminPage convertPage(Page<Payment> paymentPage, Map<Long, MemberResDto.GetMemberByLoginId> memberMap, Map<Long, String> productMap) {
        List<PaymentResDto.GetPaymentForAdmin> payments = paymentPage.stream().map(payment -> {
            Long memberId = payment.getOrder().getMemberId();
            MemberResDto.GetMemberByLoginId member = memberMap.get(memberId);

            List<String> sponsorNames = payment.getOrder().getOrderItems().stream()
                    .map(item -> productMap.get(item.getProductId()))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            return convert(payment, member, sponsorNames);
        }).toList();

        return PaymentResDto.GetPaymentForAdminPage.builder()
                .payments(payments)
                .isFirst(paymentPage.isFirst())
                .isLast(paymentPage.isLast())
                .pageSize(paymentPage.getTotalPages())
                .totalElements(paymentPage.getTotalElements())
                .build();
    }

}
