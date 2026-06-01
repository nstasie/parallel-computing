package com.coworking.booking.repository;

import com.coworking.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю User у базі даних.
 * Надає стандартні CRUD-операції через JpaRepository
 * та додаткові методи пошуку за специфічними полями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Знаходить користувача за електронною адресою. */
    Optional<User> findByEmail(String email);

    /** Перевіряє існування користувача з вказаною електронною адресою. */
    boolean existsByEmail(String email);
}
