package com.group2.parking.repository;

import com.group2.parking.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Integer> {

    //Đếm xe đang gửi thật trong bãi
    long countByBuildingIdAndVehicleTypeIdAndStatus(
            Integer buildingId,
            Integer vehicleTypeId,
            String status
    );

    // UC-406: tìm xe đang gửi theo biển số
    Optional<ParkingSession> findFirstByLicensePlateAndStatus(
            String licensePlate,
            String status
    );
}
