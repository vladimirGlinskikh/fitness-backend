package org.example.fitness_server.util;

import org.example.fitness_server.model.UserEntity;

public class UserEntityValidator {

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