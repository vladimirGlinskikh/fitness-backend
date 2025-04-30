package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями в приложении фитнес-клуба.
 * <p>
 * Этот интерфейс расширяет {@code JpaRepository}, предоставляя стандартные методы
 * для операций CRUD с сущностью {@code User}, а также дополнительный метод
 * для поиска пользователя по имени пользователя.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return объект {@code Optional}, содержащий пользователя, если он найден, или пустой, если не найден
     */

    Optional<User> findByUsername(String username);
}