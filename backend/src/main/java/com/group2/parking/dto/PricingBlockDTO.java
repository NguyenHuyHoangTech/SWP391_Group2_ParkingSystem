package com.group2.parking.dto;


import java.math.BigDecimal;


public class PricingBlockDTO {
    private Integer blockOrder;
    private Integer durationHours;
    private BigDecimal price;

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
