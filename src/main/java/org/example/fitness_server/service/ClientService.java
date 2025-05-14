package org.example.fitness_server.service;

import org.example.fitness_server.model.Client;
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
 * Сервис для управления клиентами в приложении фитнес-клуба.
 * <p>
 * Этот класс предоставляет методы для создания и обновления клиентов,
 * включая валидацию данных и синхронизацию с пользователями.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для создания экземпляра {@code ClientService}.
     *
     * @param clientRepository репозиторий для работы с клиентами
     * @param userRepository   репозиторий для работы с пользователями
     * @param passwordEncoder  кодировщик паролей для шифрования паролей
     */

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создаёт нового клиента.
     * <p>
     * Выполняет валидацию данных клиента, проверяет уникальность имени пользователя,
     * создаёт связанного пользователя с ролью {@code CLIENT} и шифрует пароль.
     * </p>
     *
     * @param client объект {@code Client} с данными нового клиента
     * @return созданный объект {@code Client}
     * @throws IllegalArgumentException если данные клиента не прошли валидацию или имя пользователя занято
     */

    public Client createClient(Client client, Long trainerId) {
        validateClient(client, true);

        // Используем утилитный метод для проверки имени и создания User
        UserUtil.checkUsernameAndCreateUser(
                client,
                Role.CLIENT,
                userRepository,
                clientRepository,
                trainerRepository,
                passwordEncoder);

        if (trainerId != null) {
            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new IllegalArgumentException("Тренер с ID " + trainerId + " не найден."));
            client.setTrainer(trainer);
        }

        // Шифрование пароля клиента
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    /**
     * Обновляет данные клиента по его идентификатору.
     * <p>
     * Выполняет валидацию данных, проверяет уникальность нового имени пользователя,
     * если оно изменено, обновляет связанного пользователя и шифрует новый пароль,
     * если он указан.
     * </p>
     *
     * @param id     идентификатор клиента
     * @param client объект {@code Client} с обновлёнными данными
     * @return обновлённый объект {@code Client}
     * @throws IllegalArgumentException если клиент с указанным ID не найден,
     *                                  данные не прошли валидацию или имя пользователя занято
     */

    public Client updateClient(Long id, Client client, Long trainerId) {
        return clientRepository.findById(id)
                .map(existing -> {
                    validateClient(client, false);

                    // Обновление имени пользователя и пароля для User
                    UserUtil.updateUserIfNeeded(existing, client, userRepository, clientRepository, trainerRepository, passwordEncoder);

                    // Обновление полей клиента
                    UserUtil.updateEntity(existing, client, passwordEncoder, () -> {
                        if (trainerId != null) {
                            Trainer trainer = trainerRepository.findById(trainerId)
                                    .orElseThrow(() -> new IllegalArgumentException("Тренер с ID " + trainerId + " не найден."));
                            existing.setTrainer(trainer);
                        } else {
                            existing.setTrainer(null);
                        }
                    });

                    existing.setPhone(client.getPhone());
                    existing.setSubscription(client.getSubscription());

                    return clientRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + id + " не найден."));
    }

    /**
     * Проверяет данные клиента на соответствие правилам.
     * <p>
     * Правила валидации:
     * <ul>
     *     <li>Имя: не пустое, 2–50 символов, только буквы, пробелы и дефисы.</li>
     *     <li>Телефон: должен начинаться с "+7" и содержать 10 цифр.</li>
     *     <li>Имя пользователя: 3–20 символов, только буквы, цифры и подчёркивания.</li>
     *     <li>Пароль: минимум 6 символов (для нового клиента обязательно, для обновления опционально).</li>
     *     <li>Абонемент: должен быть выбран.</li>
     * </ul>
     * Также форматирует имя, удаляя лишние пробелы.
     * </p>
     *
     * @param client объект {@code Client} для проверки
     * @param isNew  флаг, указывающий, создаётся ли новый клиент ({@code true}) или обновляется существующий ({@code false})
     * @throws IllegalArgumentException если данные не соответствуют правилам
     */

    private void validateClient(Client client, boolean isNew) {
        // Общая валидация для UserEntity
        UserEntityValidator.validateUserEntity(client, "клиента", isNew);
    }
}