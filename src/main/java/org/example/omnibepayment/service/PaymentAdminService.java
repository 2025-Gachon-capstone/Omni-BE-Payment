package org.example.omnibepayment.service;

import org.example.omnibepayment.dto.PaymentResDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PaymentAdminService {

    PaymentResDto.GetPaymentForAdminPage getPaymentForAdminPage(String loginId, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
