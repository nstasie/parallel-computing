package com.coworking.booking.dto;

import com.coworking.booking.model.enums.WorkspaceType;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Запит до Billing Service для створення рахунку-фактури після успішного бронювання.
 * Передається через Feign-клієнт методом POST /api/invoices/create.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInvoiceRequest {

    /** Ідентифікатор бронювання, для якого формується рахунок. */
    private Long bookingId;

    /** Тип робочого простору — визначає, який тариф буде застосовано. */
    private WorkspaceType workspaceType;

    /** Час початку бронювання для розрахунку тривалості. */
    private LocalDateTime startTime;

    /** Час завершення бронювання для розрахунку тривалості. */
    private LocalDateTime endTime;
}
