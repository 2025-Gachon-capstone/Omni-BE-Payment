package org.example.omnibepayment.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.omnibepayment.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final OrderService orderService;

    @Scheduled(cron = "0 3 0 * * *")
    public void expiredOrders(){
        orderService.expireOldPendingOrders();
    }

}
