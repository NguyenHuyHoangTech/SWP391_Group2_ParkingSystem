package com.group2.parking.repository;

import com.group2.parking.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Integer> {

    //ĐẾM XE ĐANG GỬI THẬT TRONG BÃI
    long countByBuildingIdAndVehicleTypeIdAndStatus(
            Integer buildingId,
            Integer vehicleTypeId,
            String status
    );

    // UC-406: TÌM XE ĐANG GỬI THEO BIỂN SỐ
    Optional<ParkingSession> findFirstByLicensePlateAndStatus(String licensePlate, String status);
}
