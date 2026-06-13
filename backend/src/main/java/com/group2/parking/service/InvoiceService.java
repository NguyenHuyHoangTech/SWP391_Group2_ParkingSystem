package com.group2.parking.service;

import com.group2.parking.entity.Invoice;
import com.group2.parking.entity.ParkingSession;
import com.group2.parking.exception.AppException;
import com.group2.parking.repository.InvoiceRepository;
import com.group2.parking.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
// UC-17: Tạo hóa đơn thanh toán
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    public Invoice createInvoice(Integer sessionId, Double amount) {
        // 1. Tìm xem xe có trong bãi không
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "No parking session found!"));

        // 2. Lập hóa đơn
        Invoice invoice = Invoice.builder()
                .parkingSession(session)
                .amount(amount)
                .status("UNPAID") // Mới tạo thì là chưa trả tiền
                .issuedAt(LocalDateTime.now()) // Thời điểm lập
                .build();

        // 3. Lưu xuống Database
        return invoiceRepository.save(invoice);
    }

    // UC-18: Xác nhận thu tiền thành công
    public Invoice payInvoice(Integer invoiceId) {
        // 1. Tìm tờ hóa đơn trong Database
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Invoice number not found. " + invoiceId));

        // 2. Kiểm tra cẩn thận: Lỡ khách trả tiền rồi thì không thu 2 lần
        if ("PAID".equals(invoice.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "This invoice has already been paid!");
        }

        // 3. Cập nhật trạng thái thành ĐÃ THANH TOÁN và ghi nhận thời gian trả tiền
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());

        // 4. Lưu lại thay đổi xuống SQL Server
        return invoiceRepository.save(invoice);
    }

    // UC-19: Xem lịch sử toàn bộ hóa đơn
    public List<Invoice> getAllInvoices() {
        // Lệnh findAll() của Spring Data JPA sẽ tự động lấy hết dữ liệu trong bảng invoice
        return invoiceRepository.findAll();
    }

}