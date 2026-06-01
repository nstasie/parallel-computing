package com.coworking.billing.dto;

import com.coworking.billing.model.enums.InvoiceStatus;
import lombok.*;
import java.math.BigDecimal;

/**
 * Об'єкт передачі даних (DTO) для сутності Invoice.
 * Використовується як у відповідях REST API, так і при взаємодії з Booking Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {

    private Long id;
    private Long bookingId;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
}
