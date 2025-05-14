package org.example.fitness_server.service;

import org.example.fitness_server.model.Role;
import org.example.fitness_server.model.Trainer;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.repository.UserRepository;
import org.example.fitness_server.util.UserEntityValidator;
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

                    // Обновление имени пользователя и пароля для User
                    UserUtil.updateUserIfNeeded(
                            existing,
                            trainer,
                            userRepository,
                            clientRepository,
                            trainerRepository,
                            passwordEncoder);

                    // Обновление полей тренера
                    UserUtil.updateEntity(existing, trainer, passwordEncoder, null);

                    return trainerRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Тренер с ID " + id + " не найден."));
    }

    private void validateTrainer(Trainer trainer, boolean isNew) {
        // Общая валидация для UserEntity
        UserEntityValidator.validateUserEntity(trainer, "тренера", isNew);
    }
}