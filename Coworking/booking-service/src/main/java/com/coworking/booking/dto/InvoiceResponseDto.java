package com.coworking.booking.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO для отримання відповіді від Billing Service після створення рахунку.
 * Містить мінімальний набір даних, необхідний Booking Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDto {

    private Long id;
    private Long bookingId;
    private BigDecimal totalAmount;
    private String status;
}
