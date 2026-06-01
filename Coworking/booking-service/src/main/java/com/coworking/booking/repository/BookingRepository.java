package com.coworking.booking.repository;

import com.coworking.booking.model.Booking;
import com.coworking.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторій для роботи з сутністю Booking у базі даних.
 * Містить спеціалізований JPQL-запит для перевірки конфліктів бронювань.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** Повертає всі бронювання конкретного користувача. */
    List<Booking> findByUserId(Long userId);

    /** Повертає всі бронювання для конкретного робочого місця. */
    List<Booking> findByWorkspaceId(Long workspaceId);

    /** Повертає всі бронювання з вказаним статусом. */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Перевіряє наявність активних бронювань для конкретного місця в заданий часовий проміжок.
     * Логіка перекриття інтервалів: два інтервали [A,B] та [C,D] перекриваються,
     * якщо A < D та B > C (тобто початок одного раніше кінця іншого).
     * Використовується для запобігання подвійному бронюванню.
     *
     * @param workspaceId ідентифікатор робочого місця
     * @param startTime   час початку нового бронювання
     * @param endTime     час завершення нового бронювання
     * @return true — якщо існує конфліктуюче активне бронювання
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.workspaceId = :workspaceId " +
           "AND b.status = 'ACTIVE' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsConflictingBooking(
            @Param("workspaceId") Long workspaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
