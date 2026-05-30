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

    // UC-401: tạo booking
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (!request.endTime().isAfter(request.startTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        long totalSlots = slotRepository.countUsableSlots(
                request.buildingId(),
                request.vehicleTypeId()
        );

        long activeSessions = parkingSessionRepository
                .countByBuildingIdAndVehicleTypeIdAndStatus(
                        request.buildingId(),
                        request.vehicleTypeId(),
                        "ACTIVE"
                );

        long activeBookings = bookingRepository
                .countByBuildingIdAndVehicleTypeIdAndStatusInAndEndTimeAfter(
                        request.buildingId(),
                        request.vehicleTypeId(),
                        List.of("CONFIRMED"),
                        now
                );

        //check sức chứa trước khi lưu
        if (totalSlots - activeSessions - activeBookings <= 0) {
            throw new RuntimeException("Parking is full");
        }

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

        return toBookingResponse(bookingRepository.save(booking));
    }

    // UC-407: customer hủy booking
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

    // UC-402: hủy booking quá hạn, sau này gắn scheduler gọi hàm này
    @Transactional
    public void expireOverdueBookings() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(15);

        List<Booking> bookings =
                bookingRepository.findByStatusAndStartTimeBefore("CONFIRMED", expiredBefore);

        for (Booking booking : bookings) {
            booking.setStatus("EXPIRED");
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

    public List<BookingResponse> getBookingsByAccount(Integer accountId) {
        return bookingRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream().map(this::toBookingResponse).toList();
    }
}