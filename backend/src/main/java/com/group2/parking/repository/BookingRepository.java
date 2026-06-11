package com.group2.parking.repository;

import com.group2.parking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /*
    --- KIỂM TRA BIỂN SỐ ĐÃ CÓ BOOKING CONFIRMED HAY CHƯA ---
     */
    Optional<Booking> findFirstByLicensePlateAndStatusOrderByCreatedAtDesc(
            String licensePlate,
            String status
    );

    /*
    --- UC-402: LẤY CÁC BOOKING ĐÃ QUÁ HẠN ĐỂ (EXPIRED), QUÁ THỜI GIAN CHECKIN ---
     */
    List<Booking> findByStatusAndCheckedInAtIsNullAndHoldUntilBefore(
            String status,
            LocalDateTime now
    );

    /*
    --- UC-403: ĐẾM BOOKING ĐANG GIỮ CHỖ TẠI THỜI ĐIỂM HIỆN TẠI ---
     */
    long countByBuildingIdAndVehicleTypeIdAndStatusAndCheckedInAtIsNullAndExpectedCheckinTimeLessThanEqualAndHoldUntilAfter(
            Integer buildingId,
            Integer vehicleTypeId,
            String status,
            LocalDateTime expectedBefore,
            LocalDateTime holdAfter
    );

    /*
    --- CHO UC-401: ĐẾM BOOKING BỊ TRÙNG THỜI GIAN KHI TẠO BOOKING MỚI
     */
    long countByBuildingIdAndVehicleTypeIdAndStatusAndCheckedInAtIsNullAndExpectedCheckinTimeLessThanAndHoldUntilGreaterThan(
            Integer buildingId,
            Integer vehicleTypeId,
            String status,
            LocalDateTime requestedHoldUntil,
            LocalDateTime requestedExpectedCheckinTime
    );


    /*
    --- LẤY LỊCH SỬ BOOKING CỦA 1 KHÁCH HÀNG ---
    --- BOOKING MỚI NHẤT ĐƯA LÊN ĐẦU ---
     */
    List<Booking> findByAccountIdOrderByCreatedAtDesc(Integer accountId);
}