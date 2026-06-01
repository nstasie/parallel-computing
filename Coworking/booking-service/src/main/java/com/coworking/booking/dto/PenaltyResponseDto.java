package com.coworking.booking.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO для отримання відповіді від Billing Service після нарахування штрафу.
 * Містить мінімальний набір даних, необхідний Booking Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyResponseDto {

    private Long id;
    private Long bookingId;
    private String reason;
    private BigDecimal penaltyAmount;
}
