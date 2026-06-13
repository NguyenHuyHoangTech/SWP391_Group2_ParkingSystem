package com.group2.parking.service;

import com.group2.parking.entity.FineTicket;
import com.group2.parking.entity.ParkingSession;
import com.group2.parking.exception.AppException;
import com.group2.parking.repository.FineTicketRepository;
import com.group2.parking.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FineService {
    @Autowired
    private FineTicketRepository fineRepository;
    @Autowired private ParkingSessionRepository sessionRepository;

    public FineTicket createFine(Integer sessionId, String reason) {
        // Tìm phiên đỗ xe dựa trên sessionId
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"No parking session found!"));

        // Tạo biên bản phạt
        FineTicket fine = FineTicket.builder()
                .parkingSession(session)
                .reason(reason)
                .fineAmount(100000.0)
                .status("PENDING")
                .build();

        return fineRepository.save(fine);
    }
}
