package org.example.omnibepayment.converter;

import org.example.omnibepayment.dto.OrderItemResDto;
import org.example.omnibepayment.dto.ProductResDto;
import org.example.omnibepayment.entity.OrderItem;

public class OrderItemConverter {

    public static OrderItemResDto.GetOrderItem toGetOrderItem(OrderItem orderItem, ProductResDto.GetProductList product) {
        return OrderItemResDto.GetOrderItem.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productName(product.getProductName())
                .sponsorName(product.getSponsorName())
                .quantity(orderItem.getQuantity())
                .build();
    }

}
