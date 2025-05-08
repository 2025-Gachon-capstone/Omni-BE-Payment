package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetMemberByLoginId{

        private Long memberId;
        private String loginId;
        private String memberName;

    }

}
