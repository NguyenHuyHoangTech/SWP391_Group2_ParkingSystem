package com.group2.parking.repository;

import com.group2.parking.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Integer> {

    // Xử lí module mất vé và phạt tiền
    Optional<ParkingSession> findById(Integer id);
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
