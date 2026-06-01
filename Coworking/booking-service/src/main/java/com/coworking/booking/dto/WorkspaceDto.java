package com.coworking.booking.dto;

import com.coworking.booking.model.enums.WorkspaceType;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Об'єкт передачі даних (DTO) для сутності Workspace.
 * Використовується при CRUD-операціях з робочими просторами через REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceDto {

    /** Ідентифікатор робочого простору. Не заповнюється при створенні (POST). */
    private Long id;

    @NotNull(message = "Тип робочого простору є обов'язковим")
    private WorkspaceType type;

    @NotNull(message = "Місткість є обов'язковим параметром")
    @Min(value = 1, message = "Місткість повинна бути не менше 1")
    private Integer capacity;

    /** Доступність простору. За замовчуванням true при створенні. */
    private Boolean isAvailable;
}
