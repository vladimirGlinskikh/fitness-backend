package kz.zhelezyaka.fitness_server.config;

import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.model.Role;
import kz.zhelezyaka.fitness_server.model.Subscription;
import kz.zhelezyaka.fitness_server.model.User;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import kz.zhelezyaka.fitness_server.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ClientRepository clientRepository,
                           SubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

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

        // Создаём клиента для администратора
        Client adminClient = new Client();
        adminClient.setUsername("admin");
        adminClient.setName("Админ Админов");
        adminClient.setPhone("+79999999999");
        adminClient.setSubscription(subscription1); // Или subscription2, если нужен другой абонемент
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
        maria.setSubscription(subscription2);
        clientRepository.save(maria);
    }
}