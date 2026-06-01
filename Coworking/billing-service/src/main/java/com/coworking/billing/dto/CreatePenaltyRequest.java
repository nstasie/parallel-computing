package com.coworking.billing.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Запит на нарахування штрафу, що надходить від Booking Service через Feign.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePenaltyRequest {

    /** Ідентифікатор бронювання у Booking Service. */
    private Long bookingId;

    /** Запланований час початку бронювання. */
    private LocalDateTime startTime;

    /** Причина нарахування штрафу. */
    private String reason;
}
