package org.example.omnibepayment.service;

import org.example.omnibepayment.dto.OrderReqDto;
import org.example.omnibepayment.dto.OrderResDto;
import org.example.omnibepayment.entity.Order;

public interface OrderService {

    OrderResDto.CreateOrder createOrder(OrderReqDto.CreateOrder createOrderDto);
    void updateOrderStatusToDeny(Order order);
}
