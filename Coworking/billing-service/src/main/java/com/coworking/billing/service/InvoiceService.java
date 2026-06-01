package com.coworking.billing.service;

import com.coworking.billing.dto.CreateInvoiceRequest;
import com.coworking.billing.dto.InvoiceDto;
import com.coworking.billing.exception.ResourceNotFoundException;
import com.coworking.billing.model.Invoice;
import com.coworking.billing.model.Tariff;
import com.coworking.billing.model.enums.InvoiceStatus;
import com.coworking.billing.repository.InvoiceRepository;
import com.coworking.billing.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервісний шар для управління рахунками-фактурами.
 * Розраховує загальну вартість бронювання на основі тарифу та тривалості.
 *
 * Формула розрахунку: тривалість (хвилини) / 60 × ціна_за_годину.
 * Тривалість округлюється у більший бік до найближчої хвилини.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TariffRepository tariffRepository;

    /**
     * Повертає список усіх рахунків.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Повертає рахунок за унікальним ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо рахунок не знайдено
     */
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        return toDto(invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Рахунок з ідентифікатором " + id + " не знайдено")));
    }

    /**
     * Повертає рахунок за ідентифікатором пов'язаного бронювання.
     *
     * @throws ResourceNotFoundException якщо рахунок для даного бронювання не знайдено
     */
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceByBookingId(Long bookingId) {
        return toDto(invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Рахунок для бронювання з ідентифікатором " + bookingId + " не знайдено")));
    }

    /**
     * Створює рахунок-фактуру на основі запиту від Booking Service.
     * Метод виконує такі кроки:
     * 1. Знаходить відповідний тариф за типом робочого простору.
     * 2. Розраховує тривалість бронювання в хвилинах.
     * 3. Переводить тривалість у години з округленням у більший бік.
     * 4. Множить кількість годин на ціну тарифу.
     * 5. Зберігає рахунок зі статусом PENDING.
     *
     * @param request дані бронювання від Booking Service
     * @throws ResourceNotFoundException якщо тариф для вказаного типу простору не знайдено
     */
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        Tariff tariff = tariffRepository.findByWorkspaceType(request.getWorkspaceType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Тариф для типу простору " + request.getWorkspaceType() + " не знайдено. " +
                        "Будь ласка, спочатку налаштуйте тарифи у системі"));

        // Розрахунок тривалості бронювання в хвилинах
        long durationMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        // Переведення тривалості у години з округленням у більший бік (CEILING)
        // Наприклад, 90 хвилин = 2 години (клієнт сплачує за повну годину)
        BigDecimal durationHours = BigDecimal.valueOf(durationMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);

        BigDecimal totalAmount = tariff.getPricePerHour()
                .multiply(durationHours)
                .setScale(2, RoundingMode.HALF_UP);

        Invoice invoice = Invoice.builder()
                .bookingId(request.getBookingId())
                .totalAmount(totalAmount)
                .status(InvoiceStatus.PENDING)
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Рахунок {} створено для бронювання {} на суму {} грн. " +
                        "Тривалість: {} хв., тариф: {} грн/год.",
                saved.getId(), request.getBookingId(), totalAmount,
                durationMinutes, tariff.getPricePerHour());
        return toDto(saved);
    }

    /**
     * Оновлює статус рахунку (наприклад, позначає як оплачений).
     *
     * @param id     ідентифікатор рахунку
     * @param status новий статус рахунку
     * @throws ResourceNotFoundException якщо рахунок не знайдено
     */
    public InvoiceDto updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Рахунок з ідентифікатором " + id + " не знайдено"));
        invoice.setStatus(status);
        return toDto(invoiceRepository.save(invoice));
    }

    /**
     * Видаляє рахунок за ідентифікатором.
     *
     * @throws ResourceNotFoundException якщо рахунок не знайдено
     */
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Рахунок з ідентифікатором " + id + " не знайдено");
        }
        invoiceRepository.deleteById(id);
    }

    private InvoiceDto toDto(Invoice invoice) {
        return InvoiceDto.builder()
                .id(invoice.getId())
                .bookingId(invoice.getBookingId())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .build();
    }
}
