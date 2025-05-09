package org.example.fitness_server.repository;

import org.example.fitness_server.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с абонементами в приложении фитнес-клуба.
 * <p>
 * Этот интерфейс расширяет {@code JpaRepository}, предоставляя стандартные методы
 * для операций CRUD с сущностью {@code Subscription}.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
