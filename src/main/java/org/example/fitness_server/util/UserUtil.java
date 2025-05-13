package org.example.fitness_server.util;

import org.example.fitness_server.model.Role;
import org.example.fitness_server.model.User;
import org.example.fitness_server.model.UserEntity;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserUtil {

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
}