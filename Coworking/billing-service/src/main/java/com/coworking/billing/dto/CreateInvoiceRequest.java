package com.coworking.billing.dto;

import com.coworking.billing.model.enums.WorkspaceType;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Запит на створення рахунку-фактури, що надходить від Booking Service через Feign.
 * Містить усі необхідні дані для розрахунку вартості бронювання.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInvoiceRequest {

    /** Ідентифікатор бронювання у Booking Service. */
    private Long bookingId;

    /** Тип робочого простору для вибору відповідного тарифу. */
    private WorkspaceType workspaceType;

    /** Час початку бронювання для розрахунку тривалості. */
    private LocalDateTime startTime;

    /** Час завершення бронювання для розрахунку тривалості. */
    private LocalDateTime endTime;
}
