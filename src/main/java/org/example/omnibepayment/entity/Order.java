package org.example.omnibepayment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.omnibepayment.entity.base.BaseEntity;
import org.example.omnibepayment.entity.type.OrderStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private BigDecimal orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long orderNumber;

    @Column(nullable = false)
    private int orderDow;

    @Column(nullable = false)
    private int orderHour;

    @Column(nullable = false)
    private Long daysSincePrior;

    @Version
    private Long version;

}
