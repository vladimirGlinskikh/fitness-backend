package org.example.fitness_server.service;

import org.example.fitness_server.model.Role;
import org.example.fitness_server.model.Trainer;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.repository.UserRepository;
import org.example.fitness_server.util.UserUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления тренерами в приложении фитнес-клуба.
 */
@Service
public class TrainerService {
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(ClientRepository clientRepository, TrainerRepository trainerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Trainer createTrainer(Trainer trainer) {
        validateTrainer(trainer, true);

        // Используем утилитный метод для проверки имени и создания User
        UserUtil.checkUsernameAndCreateUser(trainer, Role.TRAINER, userRepository, clientRepository, trainerRepository, passwordEncoder);

        trainer.setPassword(passwordEncoder.encode(trainer.getPassword()));
        return trainerRepository.save(trainer);
    }

    public Trainer updateTrainer(Long id, Trainer trainer) {
        return trainerRepository.findById(id)
                .map(existing -> {
                    validateTrainer(trainer, false);
                    if (!existing.getUsername().equals(trainer.getUsername())) {
                        if (userRepository.findByUsername(trainer.getUsername()).isPresent() ||
                                trainerRepository.findByUsername(trainer.getUsername()).isPresent()) {
                            throw new IllegalArgumentException("Имя пользователя уже занято.");
                        }
                        userRepository.findByUsername(existing.getUsername())
                                .ifPresent(user -> {
                                    user.setUsername(trainer.getUsername());
                                    if (trainer.getPassword() != null && !trainer.getPassword().startsWith("$2a$")) {
                                        user.setPassword(passwordEncoder.encode(trainer.getPassword()));
                                    }
                                    userRepository.save(user);
                                });
                    } else if (trainer.getPassword() != null && !trainer.getPassword().startsWith("$2a$")) {
                        userRepository.findByUsername(existing.getUsername())
                                .ifPresent(user -> {
                                    user.setPassword(passwordEncoder.encode(trainer.getPassword()));
                                    userRepository.save(user);
                                });
                    }

                    existing.setName(trainer.getName());
                    existing.setUsername(trainer.getUsername());
                    if (trainer.getPassword() != null && !trainer.getPassword().startsWith("$2a$")) {
                        existing.setPassword(passwordEncoder.encode(trainer.getPassword()));
                    }
                    return trainerRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Тренер с ID " + id + " не найден."));
    }

    private void validateTrainer(Trainer trainer, boolean isNew) {
        if (trainer.getName() == null || trainer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        String cleanedName = trainer.getName().trim().replaceAll("\\s+", " ");
        if (cleanedName.length() < 2 || cleanedName.length() > 50) {
            throw new IllegalArgumentException("Имя должно содержать от 2 до 50 символов.");
        }
        if (!cleanedName.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            throw new IllegalArgumentException("Имя может содержать только буквы, пробелы и дефисы.");
        }
        trainer.setName(cleanedName);

        if (trainer.getUsername() == null || trainer.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (!trainer.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Имя пользователя должно содержать 3–20 символов (буквы, цифры, подчёркивание).");
        }

        if (isNew) {
            String password = trainer.getPassword();
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Пароль не может быть пустым при создании тренера.");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов.");
            }
        }
    }
}