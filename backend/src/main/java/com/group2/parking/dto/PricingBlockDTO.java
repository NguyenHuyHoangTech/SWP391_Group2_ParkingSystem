package com.group2.parking.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PricingBlockDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor



public class PricingBlockDTO {
    @Id
    private Long id;
    private Integer blockOrder;
    private Integer durationHours;
    private Double price;


}
