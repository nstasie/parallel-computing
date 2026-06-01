package com.coworking.booking.model.enums;

/**
 * Перелік можливих статусів бронювання.
 * ACTIVE — бронювання активне та підтверджене.
 * CANCELLED — бронювання скасоване користувачем або адміністратором.
 * COMPLETED — бронювання успішно завершено після закінчення часу.
 */
public enum BookingStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED
}
