package com.group2.parking.repository;

import com.group2.parking.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleTypeJpaRepository extends JpaRepository<VehicleType, Integer> {
}
