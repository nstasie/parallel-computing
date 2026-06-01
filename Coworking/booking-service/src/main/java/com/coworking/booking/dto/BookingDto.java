package com.coworking.booking.dto;

import com.coworking.booking.model.enums.BookingStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Об'єкт передачі даних (DTO) для сутності Booking.
 * Використовується при CRUD-операціях з бронюваннями через REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    /** Ідентифікатор бронювання. Не заповнюється при створенні (POST). */
    private Long id;

    @NotNull(message = "Ідентифікатор користувача є обов'язковим")
    private Long userId;

    @NotNull(message = "Ідентифікатор робочого місця є обов'язковим")
    private Long workspaceId;

    @NotNull(message = "Час початку бронювання є обов'язковим")
    private LocalDateTime startTime;

    @NotNull(message = "Час завершення бронювання є обов'язковим")
    private LocalDateTime endTime;

    /** Статус бронювання. Встановлюється автоматично (ACTIVE) при створенні. */
    private BookingStatus status;
}
