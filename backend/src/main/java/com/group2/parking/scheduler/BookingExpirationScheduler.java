package com.group2.parking.scheduler;

import com.group2.parking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {

    private final BookingService bookingService;

    /*
    --- MỖI 60 GIÂY TÌM COMFIRMED VÀ CHUYỂN THÀNH EXPIRED ---
     */
    @Scheduled(fixedRate = 60_000)
    public void expireOverdueBookings() {
        bookingService.expireOverdueBookings();
    }
}
