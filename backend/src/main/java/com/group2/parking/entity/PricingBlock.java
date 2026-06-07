package com.group2.parking.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PricingBlock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PricingBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "pricing_policy_id")
    @JsonIgnoreProperties("pricingBlocks")
    @JsonIgnore
    private PricingPolicy pricingPolicy;

    @Column(name = "block_order")
    private Integer blockOrder;
    @Column(name = "duration_hours")
    private Integer durationHours;// kéo dài bao nhiêu giờ
    @Column(nullable = false)
    private double price;


}
