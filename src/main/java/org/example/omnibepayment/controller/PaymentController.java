package org.example.omnibepayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.dto.OrderReqDto;
import org.example.omnibepayment.dto.OrderResDto;
import org.example.omnibepayment.dto.PaymentReqDto;
import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm")
    @Operation(summary = "결제 인증 요청 API",
            description = " 주문 아이디는 서버에서 받은 주문 코드를 넣어주세요 ( 인증 필요 없음 )",
            tags = "Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "COMMON200-성공",content = @Content(schema = @Schema(implementation = ApiResult.class))),
    })
    public ApiResult<PaymentResDto.confirmResponse> confirmPayment(@Valid @RequestBody PaymentReqDto.ConfirmRequest confirmRequest) {

        return ApiResult.onSuccess(paymentService.confirmPayment(confirmRequest));

    }

}
