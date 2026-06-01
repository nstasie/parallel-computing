package com.coworking.billing.repository;

import com.coworking.billing.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторій для роботи з сутністю Penalty у базі даних.
 */
@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    /** Повертає всі штрафи за ідентифікатором бронювання. */
    List<Penalty> findByBookingId(Long bookingId);
}
