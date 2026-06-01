package com.coworking.billing.controller;

import com.coworking.billing.dto.CreateInvoiceRequest;
import com.coworking.billing.dto.InvoiceDto;
import com.coworking.billing.model.enums.InvoiceStatus;
import com.coworking.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контролер для управління рахунками Billing Service.
 * Базовий шлях: /api/invoices
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<InvoiceDto> getInvoiceByBookingId(@PathVariable("bookingId") Long bookingId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByBookingId(bookingId));
    }

    @PostMapping("/create")
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(
            @PathVariable("id") Long id, @RequestParam("status") InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
