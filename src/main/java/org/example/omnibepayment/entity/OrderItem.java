package org.example.omnibepayment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.omnibepayment.entity.base.BaseEntity;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OrderItem")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private int quantity;

    private int addToCartOrder;

    @Column(nullable = false)
    private Boolean reordered;

    @Version
    private Long version;

}
