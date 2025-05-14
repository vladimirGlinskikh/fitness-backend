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
 * Сервис для управления клиентами фитнес-клуба.
 * <p>
 * Этот класс предоставляет методы для создания и обновления данных клиентов,
 * включая валидацию, синхронизацию с сущностью {@code User} и назначение тренеров.
 * Использует репозитории {@code ClientRepository}, {@code UserRepository} и
 * {@code TrainerRepository}, а также утилиты {@code UserUtil} и {@code UserEntityValidator}
 * для общей логики.
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
     * Конструктор сервиса для инициализации зависимостей.
     * <p>
     * Принимает репозитории и кодировщик паролей для использования в методах сервиса.
     * </p>
     *
     * @param clientRepository   репозиторий для работы с клиентами
     * @param userRepository     репозиторий для работы с пользователями
     * @param trainerRepository  репозиторий для работы с тренерами
     * @param passwordEncoder    кодировщик паролей для шифрования
     */

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создаёт нового клиента на основе предоставленных данных.
     * <p>
     * Выполняет валидацию данных клиента, проверяет уникальность имени пользователя,
     * создаёт связанного пользователя с ролью {@code CLIENT} и шифрует пароль.
     * Если указан {@code trainerId}, назначает тренера клиенту.
     * </p>
     *
     * @param client    объект {@code Client} с данными нового клиента
     * @param trainerId идентификатор тренера (опционально, может быть {@code null})
     * @return созданный объект {@code Client}
     * @throws IllegalArgumentException если данные не прошли валидацию или имя пользователя занято
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
     * Обновляет данные существующего клиента по указанному идентификатору.
     * <p>
     * Выполняет валидацию данных, обновляет имя пользователя и пароль в связанном
     * объекте {@code User}, а также назначает или удаляет тренера при необходимости.
     * </p>
     *
     * @param id        идентификатор клиента для обновления
     * @param client    объект {@code Client} с новыми данными
     * @param trainerId идентификатор тренера (опционально, может быть {@code null})
     * @return обновлённый объект {@code Client}
     * @throws IllegalArgumentException если клиент не найден или имя пользователя занято
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
     * Выполняет валидацию данных клиента перед созданием или обновлением.
     * <p>
     * Использует утилиту {@code UserEntityValidator} для общей валидации и добавляет
     * проверки специфические для клиента (номер телефона и абонемент).
     * </p>
     *
     * @param client клиент для валидации
     * @param isNew  флаг, указывающий, создаётся ли новый клиент ({@code true}) или обновляется ({@code false})
     * @throws IllegalArgumentException если данные не соответствуют требованиям
     */

    private void validateClient(Client client, boolean isNew) {
        // Общая валидация для UserEntity
        UserEntityValidator.validateUserEntity(client, "клиента", isNew);
    }
}