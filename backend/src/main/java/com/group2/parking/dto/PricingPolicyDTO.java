package com.group2.parking.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicyDTO {

    private String name;
    private Integer vehicleTypeId;
    private String status;
    private List<PricingBlockDTO> blocks;


}
