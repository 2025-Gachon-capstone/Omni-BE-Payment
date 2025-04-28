package org.example.omnibepayment.client;

import org.example.omnibepayment.common.config.TossFeignConfig;
import org.example.omnibepayment.dto.TossReqDto;
import org.example.omnibepayment.dto.TossResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tossPaymentsClient",
        url = "${toss.url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentsClient {

    @PostMapping("/confirm")
    TossResDto.ConfirmResponse confirmPayment(@RequestBody TossReqDto.ConfirmRequest request);

    @PostMapping("/{paymentKey}/cancel")
    TossResDto.CancelResponse cancelPayment(@PathVariable("paymentKey") String paymentKey,
                                            @RequestBody TossReqDto.CancelRequest request);


}
