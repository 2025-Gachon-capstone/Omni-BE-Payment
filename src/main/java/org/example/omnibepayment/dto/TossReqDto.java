package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TossReqDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfirmRequest {

        private String orderId;
        private Long amount;
        private String paymentKey;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CancelRequest {

        private String cancelReason;

    }


}
