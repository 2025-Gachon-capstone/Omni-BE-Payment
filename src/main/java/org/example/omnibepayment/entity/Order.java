package org.example.omnibepayment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.omnibepayment.entity.base.BaseEntity;
import org.example.omnibepayment.entity.type.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String orderCode;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private BigDecimal orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long orderCount;

    @Column(nullable = false)
    private int orderDow;

    @Column(nullable = false)
    private int orderHour;

    @Column(nullable = false)
    private Long daysSincePrior;

    @Version
    private Long version;

}
