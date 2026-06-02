package com.group2.parking.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "PricingBlock")

public class PricingBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "pricing_policy_id")
    @JsonIgnoreProperties("pricingBlocks")
    private PricingPolicy pricingPolicy;

    @Column(name = "block_order")
    private Integer blockOrder;
    @Column(name = "duration_hours")
    private Integer durationHours;// kéo dài bao nhiêu giờ
    @Column(nullable = false)
    private double price;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public PricingPolicy getPricingPolicy() {
        return pricingPolicy;
    }

    public void setPricingPolicy(PricingPolicy pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    public Integer getBlockOrder() {
        return blockOrder;
    }

    public void setBlockOrder(Integer blockOrder) {
        this.blockOrder = blockOrder;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
