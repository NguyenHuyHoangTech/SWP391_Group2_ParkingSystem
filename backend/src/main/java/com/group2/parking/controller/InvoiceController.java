package com.group2.parking.controller;

import com.group2.parking.dto.request.CreateInvoiceRequest;
import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.entity.Invoice;
import com.group2.parking.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
// UC-17: Chức năng tạo hóa đơn thanh toán
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> createInvoice(@RequestBody CreateInvoiceRequest request) {
        Invoice invoice = invoiceService.createInvoice(request.getSessionId(), request.getAmount());

        ApiResponse<Invoice> response = ApiResponse.<Invoice>builder()
                .success(true)
                .message("Invoice created successfully!")
                .data(invoice)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UC-18: Xác nhận thu tiền thành công
    @PutMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Invoice>> payInvoice(@PathVariable("id") Integer id) {
        Invoice updatedInvoice = invoiceService.payInvoice(id);

        ApiResponse<Invoice> response = ApiResponse.<Invoice>builder()
                .success(true)
                .message("Payment collection confirmed successfully!")
                .data(updatedInvoice)
                .build();

        return ResponseEntity.ok(response);

    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<Invoice>>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();

        ApiResponse<List<Invoice>> response = ApiResponse.<List<Invoice>>builder()
                .success(true)
                .message("Invoice history retrieved successfully!")
                .data(invoices)
                .build();

        return ResponseEntity.ok(response);
    }
}