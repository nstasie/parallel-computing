package com.coworking.booking.client;

import com.coworking.booking.dto.CreateInvoiceRequest;
import com.coworking.booking.dto.CreatePenaltyRequest;
import com.coworking.booking.dto.InvoiceResponseDto;
import com.coworking.booking.dto.PenaltyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign-клієнт для декларативної міжсервісної взаємодії з Billing Service.
 * Spring автоматично генерує реалізацію цього інтерфейсу під час запуску.
 *
 * URL сервісу конфігурується через властивість billing.service.url в application.yml.
 * При недоступності Billing Service Feign генерує виняток, який перехоплюється
 * на рівні BookingService з відповідним логуванням помилки.
 */
@FeignClient(name = "billing-service", url = "${billing.service.url}")
public interface BillingServiceClient {

    /**
     * Відправляє запит на створення рахунку до Billing Service.
     * Викликається автоматично після успішного збереження нового бронювання.
     *
     * @param request дані бронювання для розрахунку вартості
     * @return дані створеного рахунку
     */
    @PostMapping("/api/invoices/create")
    InvoiceResponseDto createInvoice(@RequestBody CreateInvoiceRequest request);

    /**
     * Відправляє запит на нарахування штрафу до Billing Service.
     * Викликається при скасуванні бронювання менш ніж за 2 години до початку.
     *
     * @param request дані для нарахування штрафу
     * @return дані нарахованого штрафу
     */
    @PostMapping("/api/penalties/create")
    PenaltyResponseDto createPenalty(@RequestBody CreatePenaltyRequest request);
}
