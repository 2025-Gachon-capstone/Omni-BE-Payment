package org.example.omnibepayment.repository;

import org.example.omnibepayment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p " +
            "WHERE p.order.memberId = :memberId " +
            "AND (:startDate IS NULL OR p.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.createdAt <= :endDate) " +
            "AND (:orderName IS NULL OR :orderName = '' OR p.order.orderName LIKE %:orderName%)")
    Page<Payment> findPaymentsByMemberIdAndConditions(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("orderName") String orderName,
            Pageable pageable
    );

    @Query("SELECT p FROM Payment p JOIN p.order o WHERE o.memberId IN :memberIds AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByOrder_MemberIdInAndCreatedAtBetween(
            @Param("memberIds") List<Long> memberIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
