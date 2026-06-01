package com.coworking.billing.model;

import com.coworking.billing.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Сутність рахунку-фактури, що формується після успішного бронювання.
 * Містить загальну вартість, розраховану на основі тарифу та тривалості бронювання.
 * Пов'язана з бронюванням через зовнішній ідентифікатор bookingId.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    /** Унікальний ідентифікатор рахунку. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ідентифікатор пов'язаного бронювання у Booking Service. */
    @NotNull(message = "Ідентифікатор бронювання є обов'язковим")
    @Column(nullable = false)
    private Long bookingId;

    /** Загальна вартість бронювання у гривнях. */
    @NotNull(message = "Загальна сума є обов'язковою")
    @DecimalMin(value = "0.00", message = "Загальна сума не може бути від'ємною")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /** Поточний статус рахунку. За замовчуванням — PENDING (очікує оплати). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;
}
