package org.example.omnibepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderMetadata {
    private Long orderCount;
    private int orderDow;
    private int orderHour;
    private long daysSincePrior;
}
