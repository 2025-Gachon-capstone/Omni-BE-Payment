package org.example.omnibepayment.client;

import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.dto.CardReqDto;
import org.example.omnibepayment.dto.CardResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cardClient", url = "${CARD_SERVER_ADDRESS}")
public interface CardClient {

    @PostMapping("/card/v1/memberId")
    ApiResult<CardResDto.GetMemberId> getMemberId(@RequestBody CardReqDto.GetMemberId getMemberId);

}
