package com.group2.parking.controller;

import com.group2.parking.dto.BookingResponse;
import com.group2.parking.dto.CreateBookingRequest;
import com.group2.parking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/bookings")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final BookingService bookingService;

    // UC-401: Customer tạo booking
    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    //Xem booking của customer
    @GetMapping
    public List<BookingResponse> getBookings(@RequestParam("accountId") Integer accountId) {
        return bookingService.getBookingsByAccount(accountId);
    }


    // UC-407: Customer tự hủy booking
    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable("id") Integer id) {
        return bookingService.cancelBooking(id);
    }

    //UC-402
    @PostMapping("/expire-overdue")
    public String expireOverdueBookings() {
        bookingService.expireOverdueBookings();
        return "Expired overdue bookings";
    }
}