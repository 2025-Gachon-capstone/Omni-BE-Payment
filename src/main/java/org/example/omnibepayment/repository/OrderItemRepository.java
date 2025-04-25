package org.example.omnibepayment.repository;

import org.example.omnibepayment.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_MemberIdAndProductIdIn(Long memberId, List<Long> productIds);

}
