package org.example.omnibepayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class PaymentReqDto {

    @Getter
    public static class ConfirmRequest{

        @NotBlank(message = "주문 코드는 필수입니다.")
        private String orderCode;

        @NotBlank(message = "페이먼츠키는 필수입니다.")
        private String paymentKey;

        @NotNull(message = "총 주문 금액은 필수입니다.")
        private Long totalPrice;

    }

}
