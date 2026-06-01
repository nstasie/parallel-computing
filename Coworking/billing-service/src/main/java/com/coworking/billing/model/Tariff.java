package com.coworking.billing.model;

import com.coworking.billing.model.enums.WorkspaceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Сутність тарифу для конкретного типу робочого простору.
 * Визначає вартість оренди за одну годину у гривнях.
 * Для кожного типу простору може існувати лише один тариф (unique = true).
 */
@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

    /** Унікальний ідентифікатор тарифу. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Тип робочого простору, до якого застосовується даний тариф. */
    @NotNull(message = "Тип робочого простору є обов'язковим")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private WorkspaceType workspaceType;

    /** Вартість оренди за одну годину у гривнях. Повинна бути більшою за нуль. */
    @NotNull(message = "Ціна за годину є обов'язковою")
    @DecimalMin(value = "0.01", message = "Ціна повинна бути більшою за нуль")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;
}
