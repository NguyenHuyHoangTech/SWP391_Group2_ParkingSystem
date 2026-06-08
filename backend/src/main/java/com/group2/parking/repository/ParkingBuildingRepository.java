package com.group2.parking.repository;

import com.group2.parking.entity.ParkingBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingBuildingRepository extends JpaRepository<ParkingBuilding, Integer> {
}
