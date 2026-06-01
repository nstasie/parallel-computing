package com.coworking.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Сутність штрафу, що нараховується при порушенні умов бронювання.
 * Наприклад, при скасуванні менш ніж за 2 години до початку бронювання.
 */
@Entity
@Table(name = "penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty {

    /** Унікальний ідентифікатор штрафу. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ідентифікатор пов'язаного бронювання у Booking Service. */
    @NotNull(message = "Ідентифікатор бронювання є обов'язковим")
    @Column(nullable = false)
    private Long bookingId;

    /** Причина нарахування штрафу (наприклад, пізнє скасування). */
    @NotBlank(message = "Причина штрафу не може бути порожньою")
    @Column(nullable = false)
    private String reason;

    /** Сума штрафу у гривнях. Повинна бути більшою за нуль. */
    @NotNull(message = "Сума штрафу є обов'язковою")
    @DecimalMin(value = "0.01", message = "Сума штрафу повинна бути більшою за нуль")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;
}
