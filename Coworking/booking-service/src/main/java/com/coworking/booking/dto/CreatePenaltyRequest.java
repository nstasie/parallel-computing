package com.coworking.booking.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Запит до Billing Service для нарахування штрафу при порушенні умов бронювання.
 * Передається через Feign-клієнт методом POST /api/penalties/create.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePenaltyRequest {

    /** Ідентифікатор бронювання, за яким нараховується штраф. */
    private Long bookingId;

    /** Запланований час початку бронювання (для визначення терміновості скасування). */
    private LocalDateTime startTime;

    /** Причина нарахування штрафу українською мовою. */
    private String reason;
}
