package com.group2.parking.repository;

import com.group2.parking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    //UC-403: TRA CỨU SỨC CHỨA LIVE
    // Đếm booking còn hiệu lực (theo tòa nhà + loại xe) để tính sức chứa live
    // Ví dụ: buildingId = 1, vehicleTypeId = 1, status = CONFIRMED/USED, endTime > hiện tại.
    long countByBuildingIdAndVehicleTypeIdAndStatusInAndEndTimeAfter(
            Integer buildingId,
            Integer vehicleTypeId,
            Collection<String> statuses,
            LocalDateTime now
    );


    //UC-402: Lấy các booking quá hạn để xử lý EXPIRED
    //Lấy các booking đã quá hạn (CONFIRMED) nhưng startTime đã qua
    // Sau đó Service sẽ update status = EXPIRED
    List<Booking> findByStatusAndStartTimeBefore(String status, LocalDateTime time);

    //UC-401: Kiểm tra biển số đã có booking CONFIRMED gần nhất chưa.
    //Mục đích: tránh một xe đặt nhiều booking đang hiệu lực cùng lúc.
    Optional<Booking> findFirstByLicensePlateAndStatusOrderByCreatedAtDesc(
            String licensePlate,
            String status
    );

    // Dùng cho màn "My Bookings" của User.
    // Lấy toàn bộ booking của 1 account, booking mới nhất nằm trên đầu.
    // API dùng: GET /api/customer/bookings?accountId=1
    List<Booking> findByAccountIdOrderByCreatedAtDesc(Integer accountId);
}