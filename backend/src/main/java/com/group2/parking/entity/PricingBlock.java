package com.group2.parking.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "PricingBlock")
@Data
@NoArgsConstructor
public class PricingBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "block_order", nullable = false)
    private Integer blockOrder;

    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PricingPolicy pricingPolicy;
}
