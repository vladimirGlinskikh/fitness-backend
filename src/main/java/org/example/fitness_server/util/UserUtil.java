package org.example.fitness_server.util;

import org.example.fitness_server.model.Role;
import org.example.fitness_server.model.User;
import org.example.fitness_server.model.UserEntity;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Утилитный класс для работы с сущностями, реализующими интерфейс {@code UserEntity}.
 * <p>
 * Этот класс предоставляет методы для проверки уникальности имени пользователя,
 * создания и обновления связанных объектов {@code User}, а также обновления полей
 * сущностей. Используется в сервисах, таких как {@code ClientService} и {@code TrainerService},
 * для унификации логики работы с пользовательскими данными.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public class UserUtil {

    /**
     * Проверяет уникальность имени пользователя и создаёт связанный объект {@code User}.
     * <p>
     * Проверяет, не занято ли имя пользователя в таблицах пользователей, клиентов или тренеров.
     * Если имя свободно, создаётся новый объект {@code User} с указанной ролью, зашифрованным
     * паролем и сохраняется в репозитории.
     * </p>
     *
     * @param entity            сущность, реализующая {@code UserEntity}, с данными пользователя
     * @param role              роль пользователя ({@code Role.CLIENT} или {@code Role.TRAINER})
     * @param userRepository    репозиторий для работы с пользователями
     * @param clientRepository  репозиторий для работы с клиентами
     * @param trainerRepository репозиторий для работы с тренерами
     * @param passwordEncoder   кодировщик паролей для шифрования
     * @throws IllegalArgumentException если имя пользователя уже занято
     */

    public static void checkUsernameAndCreateUser(UserEntity entity, Role role, 
            UserRepository userRepository, ClientRepository clientRepository, 
            TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        // Проверка уникальности имени пользователя
        if (userRepository.findByUsername(entity.getUsername()).isPresent() ||
                clientRepository.findByUsername(entity.getUsername()).isPresent() ||
                trainerRepository.findByUsername(entity.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Имя пользователя уже занято.");
        }

        // Создание User
        User user = new User();
        user.setUsername(entity.getUsername());
        user.setPassword(passwordEncoder.encode(entity.getPassword()));
        user.setRole(role);
        userRepository.save(user);
    }

    /**
     * Обновляет связанный объект {@code User}, если изменились имя пользователя или пароль.
     * <p>
     * Если имя пользователя изменилось, проверяет его уникальность и обновляет данные в
     * объекте {@code User}. Если пароль изменился и не зашифрован, шифрует его и обновляет.
     * </p>
     *
     * @param existing          текущая сущность, реализующая {@code UserEntity}
     * @param updated           обновлённая сущность с новыми данными
     * @param userRepository    репозиторий для работы с пользователями
     * @param clientRepository  репозиторий для работы с клиентами
     * @param trainerRepository репозиторий для работы с тренерами
     * @param passwordEncoder   кодировщик паролей для шифрования
     * @throws IllegalArgumentException если новое имя пользователя уже занято
     */

    public static void updateUserIfNeeded(UserEntity existing, UserEntity updated,
                                          UserRepository userRepository, ClientRepository clientRepository,
                                          TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        if (!existing.getUsername().equals(updated.getUsername())) {
            if (userRepository.findByUsername(updated.getUsername()).isPresent() ||
                    clientRepository.findByUsername(updated.getUsername()).isPresent() ||
                    trainerRepository.findByUsername(updated.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Имя пользователя уже занято.");
            }
            userRepository.findByUsername(existing.getUsername())
                    .ifPresent(user -> {
                        user.setUsername(updated.getUsername());
                        if (updated.getPassword() != null && !updated.getPassword().startsWith("$2a$")) {
                            user.setPassword(passwordEncoder.encode(updated.getPassword()));
                        }
                        userRepository.save(user);
                    });
        } else if (updated.getPassword() != null && !updated.getPassword().startsWith("$2a$")) {
            userRepository.findByUsername(existing.getUsername())
                    .ifPresent(user -> {
                        user.setPassword(passwordEncoder.encode(updated.getPassword()));
                        userRepository.save(user);
                    });
        }
    }

    /**
     * Обновляет поля сущности на основе данных обновлённой сущности.
     * <p>
     * Обновляет имя, имя пользователя и пароль (при необходимости шифрует).
     * Если передан {@code trainerLogic}, выполняет дополнительную логику (например,
     * назначение тренера для клиента).
     * </p>
     *
     * @param existing      текущая сущность, реализующая {@code UserEntity}
     * @param updated       обновлённая сущность с новыми данными
     * @param passwordEncoder кодировщик паролей для шифрования
     * @param trainerLogic  дополнительная логика для выполнения (может быть {@code null})
     * @param <T>           тип сущности, расширяющий {@code UserEntity}
     */

    public static <T extends UserEntity> void updateEntity(T existing, T updated,
                                                           PasswordEncoder passwordEncoder, Runnable trainerLogic) {
        existing.setName(updated.getName());
        existing.setUsername(updated.getUsername());
        if (updated.getPassword() != null && !updated.getPassword().startsWith("$2a$")) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        if (trainerLogic != null) {
            trainerLogic.run();
        }
    }
}