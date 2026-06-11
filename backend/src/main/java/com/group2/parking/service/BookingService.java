package com.group2.parking.service;

import com.group2.parking.dto.response.BookingResponse;
import com.group2.parking.dto.request.CreateBookingRequest;
import com.group2.parking.entity.Booking;
import com.group2.parking.repository.BookingRepository;
import com.group2.parking.repository.ParkingSessionRepository;
import com.group2.parking.repository.ParkingZoneRepository;
import com.group2.parking.repository.SlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingZoneRepository  parkingZoneRepository;
    private final SlotRepository slotRepository;

    // UC-401: TẠO BOOKING
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        LocalDateTime now = LocalDateTime.now();

        //1. CHECK THỜI GIAN HỢP LỆ (END > START)
        if (request.expectedCheckinTime() == null || request.holdUntil() == null) {
            throw new RuntimeException("Vui lòng chọn thời gian dự kiến đến bãi và thời gian hết hạn giữ chỗ.");
        }

        //2. KHÔNG CHO BOOKING TRONG QUÁ KHỨ
        if (request.holdUntil().isBefore(request.expectedCheckinTime())) {
            throw new RuntimeException("Thời gian hết hạn giữ chỗ phải sau thời gian dự kiến đến bãi.");
        }

        if (request.expectedCheckinTime().isBefore(now)) {
            throw new RuntimeException("Thời gian dự kiến đến bãi không được nằm trong quá khứ.");
        }

        //3. KHÔNG CHO TRÙNG BIỂN SỐ
        bookingRepository.findFirstByLicensePlateAndStatusOrderByCreatedAtDesc(
                        request.licensePlate(),
                        "CONFIRMED")
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "This license plate already has a confirmed booking"
                    );
                });

        //4. ĐẾM BOOKING CONFIRMED GIAO NHAU VỚI KHOẢNG THỜI GIAN
        long overlappingBookings = bookingRepository.countByBuildingIdAndVehicleTypeIdAndStatusAndCheckedInAtIsNullAndExpectedCheckinTimeLessThanAndHoldUntilGreaterThan(
                                request.buildingId(),
                                request.vehicleTypeId(),
                                "CONFIRMED",
                                request.holdUntil(),
                                request.expectedCheckinTime());

        //5. TỔNG SỨC CHỨA
        long totalSlots = parkingZoneRepository.sumCapacity(
                request.buildingId(),
                request.vehicleTypeId()
        );

        //6. ĐẾM XE ĐANG GỬI THẬT
        long activeSessions = parkingSessionRepository
                .countByBuildingIdAndVehicleTypeIdAndStatus(
                        request.buildingId(),
                        request.vehicleTypeId(),
                        "ACTIVE"
                );

        //7. CHECK CÒN CHỖ TRONG THỜI GIAN KHÁCH YÊU CẦU
        long availableSlots = Math.max(0, totalSlots - activeSessions -  overlappingBookings);

        if (availableSlots <= 0) {
            throw new RuntimeException("Parking is full for the requested time");
        }
        //8. TẠO BOOKING
        Booking booking = Booking.builder()
                .accountId(request.accountId())
                .buildingId(request.buildingId())
                .vehicleTypeId(request.vehicleTypeId())
                .licensePlate(request.licensePlate())
                .bookingType("SHORT_TERM")
                .expectedCheckinTime(request.expectedCheckinTime())
                .holdUntil(request.holdUntil())
                .checkedInAt(null)
                .status("CONFIRMED")
                .createdAt(now)
                .build();

        //9. LƯU VÀ RETURN
        return toBookingResponse(bookingRepository.save(booking));
    }

    /*
     --- UC-402: HỦY BOOKING QUÁ HẠN ---
     --- KHÁCH ĐƯỢC PHÉP ĐẾN TRỄ TỐI ĐA 15P ---
     */

    @Transactional
    public void expireOverdueBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings =
                bookingRepository.findByStatusAndCheckedInAtIsNullAndHoldUntilBefore(
                        "CONFIRMED",
                        now);

        for (Booking booking : bookings) {
            booking.setStatus("EXPIRED");
            booking.setExpiredAt(now);
        }

        bookingRepository.saveAll(bookings);
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getAccountId(),
                booking.getBuildingId(),
                booking.getVehicleTypeId(),
                booking.getLicensePlate(),
                booking.getBookingType(),
                booking.getExpectedCheckinTime(),
                booking.getHoldUntil(),
                booking.getCheckedInAt(),
                booking.getCreatedAt(),
                booking.getStatus()
        );
    }

    // UC-407: USER HỦY BOOKING
    @Transactional
    public BookingResponse cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Only confirmed booking can be cancelled");
        }

        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());

        return toBookingResponse(bookingRepository.save(booking));
    }

    //UC: XEM DANH SÁCH BOOKING CỦA USER
    //LẤY TOÀN BỘ BOOKINF CỦA 1 USER
    //BOOKING MỚI NHẤT HIỂN THỊ LÊN ĐẦU DANH SÁCH.
    public List<BookingResponse> getBookingsByAccount(Integer accountId) {
        return bookingRepository.
                findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toBookingResponse)
                .toList();
    }
}