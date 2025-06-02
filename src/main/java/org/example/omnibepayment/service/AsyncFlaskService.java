package org.example.omnibepayment.service;

import lombok.extern.slf4j.Slf4j;
import org.example.omnibepayment.client.FlaskClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
@Slf4j
public class AsyncFlaskService {
    @Async
    public void sendOrderAsync(Long orderId, FlaskClient flaskClient) {
        try {
            StopWatch aiStopWatch = new StopWatch("AI 통신");
            aiStopWatch.start("Flask 호출");

            flaskClient.sendOrder(orderId);

            aiStopWatch.stop();
            log.info(aiStopWatch.prettyPrint());

        } catch (Exception e) {
            log.warn("Flask 호출 실패 - orderId: {}", orderId, e);
        }
    }
}
