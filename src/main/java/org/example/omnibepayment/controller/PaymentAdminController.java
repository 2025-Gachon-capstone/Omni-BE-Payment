package org.example.omnibepayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.common.apiPayload.code.status.ErrorStatus;
import org.example.omnibepayment.common.apiPayload.exception.GeneralException;
import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.service.PaymentAdminService;
import org.example.omnibepayment.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/payment/v1/admin")
public class PaymentAdminController {

    private final PaymentService paymentService;
    private final PaymentAdminService paymentAdminService;

    public PaymentAdminController(PaymentService paymentService, PaymentAdminService paymentAdminService) {
        this.paymentService = paymentService;
        this.paymentAdminService = paymentAdminService;
    }

    @GetMapping("/payments")
    @Operation(summary = "관리자 결제 내역 조회 API",
            description = " 시작 날짜(0000-00-00), 마감 날짜(0000-00-00), 로그인 아이디는 선택입니다. ( 엑세스 토큰 필요, 관리자 로그인 필요 )",
            tags = "Admin-Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "COMMON200-성공",content = @Content(schema = @Schema(implementation = ApiResult.class))),
    })
    public ApiResult<PaymentResDto.GetPaymentForAdminPage> getPaymentsForAdmin(@Parameter(hidden = true) @RequestHeader("X-Authorization-Role") String role,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                               @RequestParam(required = false) String loginId,
                                                               @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable){

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        return ApiResult.onSuccess(paymentAdminService.getPaymentForAdminPage(loginId,startDate,endDate,pageable));

    }
}
