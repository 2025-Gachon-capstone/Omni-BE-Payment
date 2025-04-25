package org.example.omnibepayment.client;

import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.dto.ProductReqDto;
import org.example.omnibepayment.dto.ProductResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "sponsorClient", url = "${SPONSOR_SERVER_ADDRESS}")
public interface SponsorClient {

    @PostMapping("/sponsor/v1/products/list")
    ApiResult<List<ProductResDto.GetProductList>> getProductList(@RequestBody ProductReqDto.GetProductList getProductList);

}
