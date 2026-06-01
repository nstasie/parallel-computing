package com.coworking.billing.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * Об'єкт передачі даних (DTO) для сутності Penalty.
 * Використовується при відображенні штрафів через REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenaltyDto {

    private Long id;
    private Long bookingId;
    private String reason;
    private BigDecimal penaltyAmount;
}
