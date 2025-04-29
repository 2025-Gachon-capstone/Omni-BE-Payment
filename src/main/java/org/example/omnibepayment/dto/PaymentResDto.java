package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class PaymentResDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class confirmResponse {

        private Long paymentId;
        private String orderCode;
        private String paymentStatus;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPayment {

        private Long paymentId;
        private String orderCode;
        private String createdAt;
        private String orderName;
        private BigDecimal paymentPrice;
        private String paymentStatus;
        private List<OrderItemResDto.GetOrderItem> orderItems;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPaymentPage {

        List<PaymentResDto.GetPayment> payments;
        boolean isFirst;
        boolean isLast;
        int pageSize;
        long totalElements;

    }


}
