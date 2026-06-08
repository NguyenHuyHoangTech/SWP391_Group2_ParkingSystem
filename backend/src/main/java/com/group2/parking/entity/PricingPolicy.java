package com.group2.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PricingPolicy")
@Data
@NoArgsConstructor
public class PricingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(length = 50)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "pricingPolicy", cascade = CascadeType.ALL)
    private List<PricingBlock> blocks = new ArrayList<>();
}
