package com.coworking.billing.repository;

import com.coworking.billing.model.Tariff;
import com.coworking.billing.model.enums.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Tariff у базі даних.
 */
@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    /** Знаходить тариф за типом робочого простору. */
    Optional<Tariff> findByWorkspaceType(WorkspaceType workspaceType);

    /** Перевіряє існування тарифу для вказаного типу простору. */
    boolean existsByWorkspaceType(WorkspaceType workspaceType);
}
