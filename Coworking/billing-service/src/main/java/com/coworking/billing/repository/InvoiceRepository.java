package com.coworking.billing.repository;

import com.coworking.billing.model.Invoice;
import com.coworking.billing.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Invoice у базі даних.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /** Знаходить рахунок за ідентифікатором бронювання. */
    Optional<Invoice> findByBookingId(Long bookingId);

    /** Повертає всі рахунки з вказаним статусом. */
    List<Invoice> findByStatus(InvoiceStatus status);
}
