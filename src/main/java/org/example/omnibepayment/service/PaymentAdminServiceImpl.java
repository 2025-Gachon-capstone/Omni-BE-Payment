package org.example.omnibepayment.service;

import lombok.extern.slf4j.Slf4j;
import org.example.omnibepayment.client.SponsorClient;
import org.example.omnibepayment.client.UserClient;
import org.example.omnibepayment.common.apiPayload.ApiResult;
import org.example.omnibepayment.common.apiPayload.code.status.ErrorStatus;
import org.example.omnibepayment.common.apiPayload.exception.GeneralException;
import org.example.omnibepayment.converter.PaymentConverter;
import org.example.omnibepayment.dto.MemberResDto;
import org.example.omnibepayment.dto.PaymentResDto;
import org.example.omnibepayment.dto.ProductReqDto;
import org.example.omnibepayment.dto.ProductResDto;
import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.Payment;
import org.example.omnibepayment.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentAdminServiceImpl implements PaymentAdminService {

    private final UserClient userClient;
    private final PaymentRepository paymentRepository;
    private final SponsorClient sponsorClient;

    public PaymentAdminServiceImpl(UserClient userClient, PaymentRepository paymentRepository,
                                   SponsorClient sponsorClient) {
        this.userClient = userClient;
        this.paymentRepository = paymentRepository;
        this.sponsorClient = sponsorClient;
    }

    @Override
    public PaymentResDto.GetPaymentForAdminPage getPaymentForAdminPage(String loginId, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Page<Payment> payments;
        Map<Long, MemberResDto.GetMemberByLoginId> userMap = new HashMap<>();

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        if (loginId != null && !loginId.isBlank()) {
            ApiResult<List<MemberResDto.GetMemberByLoginId>> response;
            try {
                response = userClient.getMemberByLoginId(loginId);
            } catch (Exception e) {
                log.error("Feign 통신 오류: 유저 서버 호출 실패", e);
                throw new GeneralException(ErrorStatus._USER_SERVICE_ERROR);
            }
            if (!response.getIsSuccess()) {
                throw new GeneralException(ErrorStatus._NOT_FOUND_MEMBER);
            }
            List<MemberResDto.GetMemberByLoginId> users = response.getResult();
            List<Long> memberIds = users.stream().map(MemberResDto.GetMemberByLoginId::getMemberId).toList();
            userMap = users.stream().collect(Collectors.toMap(MemberResDto.GetMemberByLoginId::getMemberId,Function.identity() ));

            payments = paymentRepository.findByOrder_MemberIdInAndCreatedAtBetween(memberIds, startDateTime, endDateTime, pageable);
        } else {
            payments = paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable);
            List<Long> memberIds = payments.stream()
                    .map(Payment::getOrder)
                    .filter(Objects::nonNull)
                    .map(Order::getMemberId)
                    .distinct()
                    .toList();

            ApiResult<List<MemberResDto.GetMemberByLoginId>> memberRes;
            try {
                memberRes = userClient.getMemberList(memberIds);
            } catch (Exception e) {
                log.error("Feign 통신 오류: 유저 서버 호출 실패", e);
                throw new GeneralException(ErrorStatus._USER_SERVICE_ERROR);
            }

            if (!memberRes.getIsSuccess()) {
                throw new GeneralException(ErrorStatus._USER_SERVICE_ERROR);
            }

            userMap = memberRes.getResult().stream()
                    .collect(Collectors.toMap(MemberResDto.GetMemberByLoginId::getMemberId, Function.identity()));
        }

        List<Long> productIds = payments.stream()
                .flatMap(payment -> payment.getOrder().getOrderItems().stream())
                .map(orderItem -> orderItem.getProductId())
                .distinct()
                .collect(Collectors.toList());

        ProductReqDto.GetProductList request = ProductReqDto.GetProductList.builder()
                .productIds(productIds)
                .build();

        ApiResult<List<ProductResDto.GetProductList>> productResponse;

        try {
            productResponse = sponsorClient.getProductList(request);
        } catch (Exception e) {
            log.error("Feign 통신 오류: 스폰서 서버 호출 실패", e);
            throw new GeneralException(ErrorStatus._SPONSOR_SERVICE_ERROR);
        }


        List<ProductResDto.GetProductList> products = productResponse.getResult();

        Map<Long, String> sponsorMap = PaymentConverter.toProductSponsorNameMap(products);
        return PaymentConverter.convertPage(payments, userMap, sponsorMap);
    }
}
