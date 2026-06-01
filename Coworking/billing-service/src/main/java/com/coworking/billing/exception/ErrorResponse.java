package com.coworking.billing.exception;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Уніфікована структура відповіді при виникненні помилок у Billing Service.
 * Забезпечує єдиний формат повідомлень про помилки для клієнтів API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /** Часова мітка виникнення помилки. */
    private LocalDateTime timestamp;

    /** HTTP-статус код відповіді. */
    private int status;

    /** Повідомлення про помилку українською мовою. */
    private String message;

    /** Шлях HTTP-запиту, при обробці якого виникла помилка. */
    private String path;
}
