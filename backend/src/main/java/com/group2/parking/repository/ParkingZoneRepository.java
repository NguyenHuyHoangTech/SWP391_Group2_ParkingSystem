package com.group2.parking.repository;

import com.group2.parking.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Integer> {

    List<ParkingZone> findByFloorId(Integer floorId);
    boolean existsByFloorId(Integer floorId);

    /*
    --- UC-403 ---
    ---Tính tổng sức chứa theo buildingId và vehicleTypeId---
     */
    @Query("""
            SELECT COALESCE(SUM(z.capacity), 0)
            FROM ParkingZone z
            WHERE z.floor.building.id = :buildingId
                AND z.vehicleType.id = :vehicleType
            """)
    long sumCapacity(
            @Param("buildingId") Integer buildingId,
            @Param("vehicleType") Integer vehicleType
    );
}
