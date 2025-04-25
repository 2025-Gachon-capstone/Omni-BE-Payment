package org.example.omnibepayment.repository;


import org.example.omnibepayment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
    Long countByMemberId(Long memberId);

}
