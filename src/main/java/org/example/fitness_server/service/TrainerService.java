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
 * Сервис для управления тренерами фитнес-клуба.
 * <p>
 * Этот класс предоставляет методы для создания и обновления данных тренеров,
 * включая валидацию, синхронизацию с сущностью {@code User} и шифрование паролей.
 * Использует репозитории {@code TrainerRepository}, {@code UserRepository} и
 * {@code ClientRepository}, а также утилиты {@code UserUtil} и {@code UserEntityValidator}
 * для общей логики.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Service
public class TrainerService {
    private final ClientRepository clientRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса для инициализации зависимостей.
     * <p>
     * Принимает репозитории и кодировщик паролей для использования в методах сервиса.
     * </p>
     *
     * @param clientRepository   репозиторий для работы с клиентами
     * @param trainerRepository  репозиторий для работы с тренерами
     * @param userRepository     репозиторий для работы с пользователями
     * @param passwordEncoder    кодировщик паролей для шифрования
     */

    public TrainerService(ClientRepository clientRepository, TrainerRepository trainerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создаёт нового тренера на основе предоставленных данных.
     * <p>
     * Выполняет валидацию данных тренера, проверяет уникальность имени пользователя,
     * создаёт связанного пользователя с ролью {@code TRAINER} и шифрует пароль.
     * </p>
     *
     * @param trainer объект {@code Trainer} с данными нового тренера
     * @return созданный объект {@code Trainer}
     * @throws IllegalArgumentException если данные не прошли валидацию или имя пользователя занято
     */

    public Trainer createTrainer(Trainer trainer) {
        validateTrainer(trainer, true);

        // Используем утилитный метод для проверки имени и создания User
        UserUtil.checkUsernameAndCreateUser(trainer, Role.TRAINER, userRepository, clientRepository, trainerRepository, passwordEncoder);

        trainer.setPassword(passwordEncoder.encode(trainer.getPassword()));
        return trainerRepository.save(trainer);
    }

    /**
     * Обновляет данные существующего тренера по указанному идентификатору.
     * <p>
     * Выполняет валидацию данных, обновляет имя пользователя и пароль в связанном
     * объекте {@code User}, а также обновляет остальные поля тренера.
     * </p>
     *
     * @param id      идентификатор тренера для обновления
     * @param trainer объект {@code Trainer} с новыми данными
     * @return обновлённый объект {@code Trainer}
     * @throws IllegalArgumentException если тренер с указанным ID не найден или имя пользователя занято
     */

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

    /**
     * Выполняет валидацию данных тренера перед созданием или обновлением.
     * <p>
     * Использует утилиту {@code UserEntityValidator} для общей валидации полей
     * ({@code name}, {@code username}, {@code password}).
     * </p>
     *
     * @param trainer тренер для валидации
     * @param isNew   флаг, указывающий, создаётся ли новый тренер ({@code true}) или обновляется ({@code false})
     * @throws IllegalArgumentException если данные не соответствуют требованиям
     */

    private void validateTrainer(Trainer trainer, boolean isNew) {
        // Общая валидация для UserEntity
        UserEntityValidator.validateUserEntity(trainer, "тренера", isNew);
    }
}