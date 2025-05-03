package kz.zhelezyaka.fitness_server.service;

import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.model.Role;
import kz.zhelezyaka.fitness_server.model.Trainer;
import kz.zhelezyaka.fitness_server.model.User;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.repository.TrainerRepository;
import kz.zhelezyaka.fitness_server.repository.UserRepository;
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

        // Проверка уникальности username
        if (userRepository.findByUsername(client.getUsername()).isPresent() ||
                clientRepository.findByUsername(client.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Имя пользователя уже занято.");
        }

        // Создание User
        User user = new User();
        user.setUsername(client.getUsername());
        user.setPassword(passwordEncoder.encode(client.getPassword()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

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
                    // Проверка уникальности username, если он изменён
                    if (!existing.getUsername().equals(client.getUsername())) {
                        if (userRepository.findByUsername(client.getUsername()).isPresent() ||
                                clientRepository.findByUsername(client.getUsername()).isPresent()) {
                            throw new IllegalArgumentException("Имя пользователя уже занято.");
                        }
                        // Обновление User
                        userRepository.findByUsername(existing.getUsername())
                                .ifPresent(user -> {
                                    user.setUsername(client.getUsername());
                                    if (client.getPassword() != null && !client.getPassword().startsWith("$2a$")) {
                                        user.setPassword(passwordEncoder.encode(client.getPassword()));
                                    }
                                    userRepository.save(user);
                                });
                    } else if (client.getPassword() != null && !client.getPassword().startsWith("$2a$")) {
                        // Обновление пароля, если username не изменился
                        userRepository.findByUsername(existing.getUsername())
                                .ifPresent(user -> {
                                    user.setPassword(passwordEncoder.encode(client.getPassword()));
                                    userRepository.save(user);
                                });
                    }

                    existing.setName(client.getName());
                    existing.setPhone(client.getPhone());
                    existing.setUsername(client.getUsername());
                    if (client.getPassword() != null && !client.getPassword().startsWith("$2a$")) {
                        existing.setPassword(passwordEncoder.encode(client.getPassword()));
                    }
                    existing.setSubscription(client.getSubscription());

                    if (trainerId != null) {
                        Trainer trainer = trainerRepository.findById(trainerId)
                                .orElseThrow(() -> new IllegalArgumentException("Тренер с ID " + trainerId + " не найден."));
                        existing.setTrainer(trainer);
                    } else {
                        existing.setTrainer(null);
                    }

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
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым.");
        }
        String cleanedName = client.getName().trim().replaceAll("\\s+", " ");
        if (cleanedName.length() < 2 || cleanedName.length() > 50) {
            throw new IllegalArgumentException("Имя должно содержать от 2 до 50 символов.");
        }
        if (!cleanedName.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            throw new IllegalArgumentException("Имя может содержать только буквы, пробелы и дефисы.");
        }
        client.setName(cleanedName);

        if (client.getPhone() == null || !client.getPhone().matches("\\+7\\d{10}")) {
            throw new IllegalArgumentException("Номер телефона должен начинаться с +7 и содержать 10 цифр.");
        }

        if (client.getUsername() == null || client.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (!client.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Имя пользователя должно содержать 3–20 символов (буквы, цифры, подчёркивание).");
        }

        if (isNew) {
            if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Пароль не может быть пустым при создании клиента.");
            }
            if (client.getPassword().length() < 6) {
                throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов.");
            }
        }

        if (client.getSubscription() == null) {
            throw new IllegalArgumentException("Абонемент должен быть выбран.");
        }
    }
}