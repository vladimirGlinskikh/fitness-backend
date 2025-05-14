package org.example.fitness_server.util;

import org.example.fitness_server.model.UserEntity;

/**
 * Утилитный класс для валидации сущностей, реализующих интерфейс {@code UserEntity}.
 * <p>
 * Этот класс предоставляет методы для проверки данных пользователей, таких как имя,
 * имя пользователя и пароль, перед их созданием или обновлением. Используется в
 * сервисах для унификации валидации сущностей, таких как {@code Client} и {@code Trainer}.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

public class UserEntityValidator {

    /**
     * Проверяет данные сущности, реализующей {@code UserEntity}, перед созданием или обновлением.
     * <p>
     * Выполняет валидацию имени, имени пользователя и пароля (для новых сущностей).
     * Имя очищается от лишних пробелов и проверяется на соответствие формату.
     * Имя пользователя должно быть уникальным и соответствовать заданному шаблону.
     * Пароль проверяется только при создании новой сущности.
     * </p>
     *
     * @param entity     сущность для валидации, реализующая {@code UserEntity}
     * @param entityType тип сущности (например, "клиента" или "тренера") для формирования сообщений об ошибках
     * @param isNew      флаг, указывающий, создаётся ли новая сущность ({@code true}) или обновляется ({@code false})
     * @throws IllegalArgumentException если данные не соответствуют требованиям
     */

    public static void validateUserEntity(UserEntity entity, String entityType, boolean isNew) {
        // Валидация имени
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        String cleanedName = entity.getName().trim().replaceAll("\\s+", " ");
        if (cleanedName.length() < 2 || cleanedName.length() > 50) {
            throw new IllegalArgumentException("Имя должно содержать от 2 до 50 символов.");
        }
        if (!cleanedName.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            throw new IllegalArgumentException("Имя может содержать только буквы, пробелы и дефисы.");
        }
        entity.setName(cleanedName);

        // Валидация имени пользователя
        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (!entity.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Имя пользователя должно содержать 3–20 символов (буквы, цифры, подчёркивание).");
        }

        // Валидация пароля
        if (isNew) {
            String password = entity.getPassword();
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Пароль не может быть пустым при создании " + entityType + ".");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов.");
            }
        }
    }
}