package com.group2.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;



import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PricingPolicy")

public class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(length = 50)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "pricingPolicy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("pricingPolicy")
    private List<PricingBlock> blocks = new ArrayList<>(); // danh sách block kèm

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PricingBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<PricingBlock> blocks) {
        this.blocks = blocks;
    }
public class PricingPolicy {
}
