package org.example.omnibepayment.service;

import org.example.omnibepayment.dto.PaymentReqDto;
import org.example.omnibepayment.dto.PaymentResDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PaymentService {
    PaymentResDto.confirmResponse confirmPayment(PaymentReqDto.ConfirmRequest confirmRequest);
    PaymentResDto.GetPaymentPage getPayments(Long memberId, LocalDate startDate, LocalDate endDate, String orderName, Pageable pageable);
}
