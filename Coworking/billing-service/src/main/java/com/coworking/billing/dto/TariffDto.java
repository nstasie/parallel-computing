package com.coworking.billing.dto;

import com.coworking.billing.model.enums.WorkspaceType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Об'єкт передачі даних (DTO) для сутності Tariff.
 * Використовується при CRUD-операціях з тарифами через REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffDto {

    /** Ідентифікатор тарифу. Не заповнюється при створенні (POST). */
    private Long id;

    @NotNull(message = "Тип робочого простору є обов'язковим")
    private WorkspaceType workspaceType;

    @NotNull(message = "Ціна за годину є обов'язковою")
    @DecimalMin(value = "0.01", message = "Ціна повинна бути більшою за нуль")
    private BigDecimal pricePerHour;
}
