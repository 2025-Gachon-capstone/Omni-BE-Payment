package org.example.omnibepayment.service;

import org.example.omnibepayment.dto.PaymentReqDto;
import org.example.omnibepayment.dto.PaymentResDto;

public interface PaymentService {
    PaymentResDto.confirmResponse confirmPayment(PaymentReqDto.ConfirmRequest confirmRequest);
}
