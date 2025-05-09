package org.example.fitness_server.config;

import org.example.fitness_server.model.Client;
import org.example.fitness_server.model.Role;
import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.model.User;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.SubscriptionRepository;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Класс для инициализации начальных данных приложения фитнес-клуба.
 * <p>
 * Этот класс реализует интерфейс {@code CommandLineRunner} и используется для заполнения базы данных
 * тестовыми данными при запуске приложения. Создаёт абонементы, администратора и тестовых клиентов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrainerRepository trainerRepository;

    /**
     * Конструктор для создания экземпляра {@code DataInitializer}.
     *
     * @param userRepository         репозиторий для работы с пользователями
     * @param clientRepository       репозиторий для работы с клиентами
     * @param subscriptionRepository репозиторий для работы с абонементами
     * @param passwordEncoder        кодировщик паролей для шифрования паролей пользователей
     */

    public DataInitializer(UserRepository userRepository, ClientRepository clientRepository,
                           SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder, TrainerRepository trainerRepository, TrainerRepository trainerRepository1) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.trainerRepository = trainerRepository1;
    }

    /**
     * Инициализирует начальные данные при запуске приложения.
     * <p>
     * Удаляет все существующие данные из репозиториев и создаёт:
     * <ul>
     *     <li>Два абонемента: "Месячный" (5000 руб., 30 дней) и "Годовой" (45000 руб., 365 дней).</li>
     *     <li>Администратора с именем пользователя "admin" и паролем "admin123".</li>
     *     <li>Двух клиентов: "Иван Иванов" (ivan, ivan123) и "Мария Петрова" (maria, maria123).</li>
     * </ul>
     * Пароли шифруются с использованием {@code PasswordEncoder}.
     *
     * @param args аргументы командной строки (не используются)
     */

    @Override
    public void run(String... args) {
        // Очищаем все данные из репозиториев
        clientRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        subscriptionRepository.deleteAllInBatch();

        // Создаём абонементы
        Subscription subscription1 = new Subscription();
        subscription1.setType("Месячный");
        subscription1.setCost(5000.00);
        subscription1.setDurationDays(30);
        subscriptionRepository.save(subscription1);

        Subscription subscription2 = new Subscription();
        subscription2.setType("Годовой");
        subscription2.setCost(45000.00);
        subscription2.setDurationDays(365);
        subscriptionRepository.save(subscription2);

        // Создаём администратора
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        Client adminClient = new Client();
        adminClient.setUsername("admin");
        adminClient.setName("Админ Админов");
        adminClient.setPhone("+79999999999");
        adminClient.setPassword(passwordEncoder.encode("admin123"));
        adminClient.setSubscription(subscription1);
        clientRepository.save(adminClient);

        // Создаём клиентов
        User client1 = new User();
        client1.setUsername("ivan");
        client1.setPassword(passwordEncoder.encode("ivan123"));
        client1.setRole(Role.CLIENT);
        userRepository.save(client1);

        Client ivan = new Client();
        ivan.setUsername("ivan");
        ivan.setName("Иван Иванов");
        ivan.setPhone("+79876543210");
        ivan.setPassword(passwordEncoder.encode("ivan123"));
        ivan.setSubscription(subscription1);
        clientRepository.save(ivan);

        User client2 = new User();
        client2.setUsername("maria");
        client2.setPassword(passwordEncoder.encode("maria123"));
        client2.setRole(Role.CLIENT);
        userRepository.save(client2);

        Client maria = new Client();
        maria.setUsername("maria");
        maria.setName("Мария Петрова");
        maria.setPhone("+79991234567");
        maria.setPassword(passwordEncoder.encode("maria123"));
        maria.setSubscription(subscription2);
        clientRepository.save(maria);
    }
}