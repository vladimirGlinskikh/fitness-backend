package org.example.fitness_server.repository;

import org.example.fitness_server.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с данными тренеров фитнес-клуба.
 * <p>
 * Этот интерфейс расширяет {@code JpaRepository} для предоставления стандартных методов
 * CRUD-операций над сущностью {@code Trainer}. Также содержит пользовательский метод
 * для поиска тренера по имени пользователя. Все операции выполняются над таблицей
 * {@code trainers} в базе данных.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * Находит тренера по указанному имени пользователя.
     * <p>
     * Метод возвращает объект {@code Optional}, содержащий тренера, если он найден,
     * или пустой {@code Optional}, если тренер с таким именем пользователя отсутствует.
     * </p>
     *
     * @param username имя пользователя для поиска
     * @return {@code Optional} с объектом {@code Trainer} или пустой {@code Optional}
     */

    Optional<Trainer> findByUsername(String username);
}