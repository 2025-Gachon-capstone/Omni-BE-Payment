package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class TossResDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfirmResponse {

        private String orderId;
        private String paymentKey;
        private String orderName;
        private int totalAmount;
        private String approvedAt;
        private String status;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CancelResponse {

        private String orderId;
        private String paymentKey;
        private String orderName;
        private String status;
        private List<CancelDetail> cancels;

        @Getter
        @NoArgsConstructor
        public static class CancelDetail {
            private String cancelReason;
            private String canceledAt;
            private BigDecimal cancelAmount;
            private String cancelStatus;
        }

    }



}
