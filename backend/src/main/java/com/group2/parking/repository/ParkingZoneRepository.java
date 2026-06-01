package com.group2.parking.repository;

import com.group2.parking.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Integer> {
    List<ParkingZone> findByFloorId(Integer floorId);
    boolean existsByFloorId(Integer floorId);
}
