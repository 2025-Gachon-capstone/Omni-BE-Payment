package org.example.omnibepayment.repository;


import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.type.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
    Long countByMemberId(Long memberId);
    Optional<Order> findByOrderCode(String orderCode);
    Optional<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :createdAt")
    List<Order> findExpiredPendingOrders(@Param("status") OrderStatus status,
                                         @Param("createdAt") LocalDateTime createdAt);

}
