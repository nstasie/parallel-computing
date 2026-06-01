package com.coworking.booking.model;

import com.coworking.booking.model.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Сутність бронювання робочого простору.
 * Пов'язує користувача з конкретним робочим місцем на визначений часовий проміжок.
 * Зберігає ідентифікатори userId та workspaceId замість прямих зв'язків JPA,
 * оскільки в мікросервісній архітектурі дані можуть зберігатися в різних базах.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    /** Унікальний ідентифікатор бронювання. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ідентифікатор користувача, який здійснив бронювання. */
    @NotNull(message = "Ідентифікатор користувача є обов'язковим")
    @Column(nullable = false)
    private Long userId;

    /** Ідентифікатор забронованого робочого простору. */
    @NotNull(message = "Ідентифікатор робочого місця є обов'язковим")
    @Column(nullable = false)
    private Long workspaceId;

    /** Дата та час початку бронювання. */
    @NotNull(message = "Час початку бронювання є обов'язковим")
    @Column(nullable = false)
    private LocalDateTime startTime;

    /** Дата та час завершення бронювання. */
    @NotNull(message = "Час завершення бронювання є обов'язковим")
    @Column(nullable = false)
    private LocalDateTime endTime;

    /** Поточний статус бронювання. За замовчуванням — ACTIVE. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.ACTIVE;
}
