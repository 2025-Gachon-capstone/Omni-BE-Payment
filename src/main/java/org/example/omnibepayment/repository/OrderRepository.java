package org.example.omnibepayment.repository;


import org.example.omnibepayment.entity.Order;
import org.example.omnibepayment.entity.type.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
    Long countByMemberId(Long memberId);
    Optional<Order> findByOrderCode(String orderCode);
    Optional<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status);
}
