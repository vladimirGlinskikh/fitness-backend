package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с клиентами в приложении фитнес-клуба.
 * <p>
 * Этот интерфейс расширяет {@code JpaRepository}, предоставляя стандартные методы
 * для операций CRUD с сущностью {@code Client}, а также дополнительный метод
 * для поиска клиента по имени пользователя.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Находит клиента по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return объект {@code Optional}, содержащий клиента, если он найден, или пустой, если не найден
     */

    Optional<Client> findByUsername(String username);
}
