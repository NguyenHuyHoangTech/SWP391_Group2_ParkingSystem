package com.group2.parking.repository;

import com.group2.parking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlotRepository extends JpaRepository<Slot, Integer> {

    boolean existsByZoneId(Integer zoneId);

    // ĐẾM TỔNG SLOT DÙNG ĐƯỢC THEO BÃI XE VÀ LOẠI XE
    @Query(value = """
        SELECT COUNT(s.id)
        FROM Slot s
        JOIN ParkingZone z ON s.zone_id = z.id
        JOIN Floor f ON z.floor_id = f.id
        WHERE f.building_id = :buildingId
          AND z.vehicle_type_id = :vehicleTypeId
          AND s.status <> 'MAINTENANCE'
    """, nativeQuery = true)
    long countUsableSlots(
            @Param("buildingId") Integer buildingId,
            @Param("vehicleTypeId") Integer vehicleTypeId
    );
}