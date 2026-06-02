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
public class UserBookingController {

    private final BookingService bookingService;

    // UC-401: USER TẠO BOOKING
    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    //UC-402: HỦY BOOKING QUÁ HẠN
    @PostMapping("/expire-overdue")
    public String expireOverdueBookings() {
        bookingService.expireOverdueBookings();
        return "Expired overdue bookings";
    }

    //XEM BOOKING CỦA USER
    @GetMapping
    public List<BookingResponse> getBookings(@RequestParam("accountId") Integer accountId) {
        return bookingService.getBookingsByAccount(accountId);
    }


    // UC-407: USER TỰ HỦY BOOKING
    @PatchMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable("id") Integer id) {
        return bookingService.cancelBooking(id);
    }

}