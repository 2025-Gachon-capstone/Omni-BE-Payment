package org.example.omnibepayment.service;

import org.example.omnibepayment.dto.OrderReqDto;
import org.example.omnibepayment.dto.OrderResDto;

public interface OrderService {

    OrderResDto.CreateOrder createOrder(OrderReqDto.CreateOrder createOrderDto);

}
