package org.example.omnibepayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OrderReqDto {

    @Getter
    public static class CreateOrder{

        @NotBlank(message = "카드 번호는 필수입니다.")
        private String cardNumber;

        @NotBlank(message = "주문명은 필수입니다.")
        private String orderName;

        @NotNull(message = "주문 가격은 필수입니다.")
        private BigDecimal orderPrice;

        @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다.")
        private List<Item> items;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item {
            private Long productId;
            private int quantity;
            private int addToCartOrder;
        }
    }

}
