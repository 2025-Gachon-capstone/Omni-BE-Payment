package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
