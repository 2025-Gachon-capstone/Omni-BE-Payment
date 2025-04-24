package org.example.omnibepayment.repository;


import org.example.omnibepayment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
