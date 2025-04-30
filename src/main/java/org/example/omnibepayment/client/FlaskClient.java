package org.example.omnibepayment.client;

import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "flaskClient", url = "${AI_SERVER_ADDRESS}")
public interface FlaskClient {

    @PostMapping("/flask/v1/orders/{order_id}")
    void sendOrder(@PathVariable("order_id") Long orderId);

}

