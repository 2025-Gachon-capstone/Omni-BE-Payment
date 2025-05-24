package org.example.omnibepayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
//@EntityScan(basePackages = "org.example.omnibepayment.entity")
@EnableJpaRepositories(basePackages = "org.example.omnibepayment.repository")
public class OmniBePaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmniBePaymentApplication.class, args);
    }

}
