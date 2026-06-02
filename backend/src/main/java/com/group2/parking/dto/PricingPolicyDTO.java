package com.group2.parking.dto;



import java.util.List;

public class PricingPolicyDTO {

    private String name;
    private Integer vehicleTypeId;
    private String status;
    private List<PricingBlockDTO> blocks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PricingBlockDTO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<PricingBlockDTO> blocks) {
        this.blocks = blocks;
    }
}
