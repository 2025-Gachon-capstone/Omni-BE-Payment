package org.example.omnibepayment.converter;

import org.example.omnibepayment.dto.OrderMetadata;
import org.example.omnibepayment.dto.OrderReqDto;
import org.example.omnibepayment.dto.OrderResDto;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.type.OrderStatus;

public class OrderConverter {

    public static Order toOrder(OrderReqDto.CreateOrder reqDto, Long memberId, String orderCode, OrderMetadata metadata) {

        return Order.builder()
                .memberId(memberId)
                .orderCode(orderCode)
                .orderName(reqDto.getOrderName())
                .orderPrice(reqDto.getOrderPrice())
                .status(OrderStatus.PENDING)
                .orderCount(metadata.getOrderCount())
                .orderDow(metadata.getOrderDow())
                .orderHour(metadata.getOrderHour())
                .daysSincePrior(metadata.getDaysSincePrior())
                .build();
    }

    public static OrderResDto.CreateOrder toCreateOrder(Order order) {
        return OrderResDto.CreateOrder.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getOrderCode())
                .orderName(order.getOrderName())
                .orderPrice(order.getOrderPrice())
                .build();
    }

}
