package com.coworking.booking.repository;

import com.coworking.booking.model.Workspace;
import com.coworking.booking.model.enums.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторій для роботи з сутністю Workspace у базі даних.
 * Надає методи фільтрації за типом та доступністю.
 */
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    /** Повертає список усіх доступних для бронювання просторів. */
    List<Workspace> findByIsAvailableTrue();

    /** Повертає список просторів за вказаним типом. */
    List<Workspace> findByType(WorkspaceType type);

    /** Повертає доступні простори за вказаним типом. */
    List<Workspace> findByTypeAndIsAvailableTrue(WorkspaceType type);
}
