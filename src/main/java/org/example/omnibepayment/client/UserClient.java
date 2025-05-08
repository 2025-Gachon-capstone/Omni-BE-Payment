package org.example.omnibepayment.client;

import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.dto.MemberResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "userClient", url = "${USER_SERVER_ADDRESS}")
public interface UserClient {

    @GetMapping("/user/v1/auth/id")
    ApiResult<List<MemberResDto.GetMemberByLoginId>> getMemberByLoginId(@RequestParam("loginId") String loginId);

    @PostMapping("/user/v1/auth/memberList")
    ApiResult<List<MemberResDto.GetMemberByLoginId>> getMemberList(@RequestBody List<Long>memberIds);

}
