package com.group2.parking.service;

import com.group2.parking.dto.BookingResponse;
import com.group2.parking.dto.CreateBookingRequest;
import com.group2.parking.entity.Booking;
import com.group2.parking.repository.BookingRepository;
import com.group2.parking.repository.ParkingSessionRepository;
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
    private final SlotRepository slotRepository;

    // UC-401: TẠO BOOKING
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        LocalDateTime now = LocalDateTime.now();

        //1. CHECK THỜI GIAN HỢP LỆ (END > START)
        if (!request.endTime().isAfter(request.startTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        //2. KHÔNG CHO BOOKING TRONG QUÁ KHỨ
        if (request.startTime().isBefore(now)) {
            throw new RuntimeException("Start time must be in the future");
        }


        //3. KHÔNG CHO TRÙNG BIỂN SỐ
        bookingRepository
                .findFirstByLicensePlateAndStatusOrderByCreatedAtDesc(
                        request.licensePlate(),
                        "CONFIRMED"
                )
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "This license plate already has a confirmed booking"
                    );
                });

        //4. ĐẾM BOOKING ACTIVE
        long activeBookings =
                bookingRepository.countByBuildingIdAndVehicleTypeIdAndStatusInAndEndTimeAfter(
                request.buildingId(),
                request.vehicleTypeId(),
                List.of("CONFIRMED"),
                now
        );

        //5. DẾM TỔNG SLOT DÙNG ĐƯỢC
        long totalSlots = slotRepository.countUsableSlots(
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

        //7. Check còn chỗ không
        if (totalSlots - activeSessions - activeBookings <= 0) {
            throw new RuntimeException("Parking is full");
        }

        //8. TẠO BOOKING
        Booking booking = Booking.builder()
                .accountId(request.accountId())
                .buildingId(request.buildingId())
                .vehicleTypeId(request.vehicleTypeId())
                .licensePlate(request.licensePlate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status("CONFIRMED")
                .createdAt(now)
                .build();

        //9. LƯU VÀ RETURN
        return toBookingResponse(bookingRepository.save(booking));
    }

    // UC-402: hủy booking quá hạn, sau này gắn scheduler gọi hàm này
    @Transactional
    public void expireOverdueBookings() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(15);

        List<Booking> bookings =
                bookingRepository.findByStatusAndStartTimeBefore("CONFIRMED", expiredBefore);

        for (Booking booking : bookings) {
            booking.setStatus("EXPIRED");
            booking.setExpiredAt(LocalDateTime.now());
        }
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getAccountId(),
                booking.getBuildingId(),
                booking.getVehicleTypeId(),
                booking.getLicensePlate(),
                booking.getStartTime(),
                booking.getEndTime(),
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