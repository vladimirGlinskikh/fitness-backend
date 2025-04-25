package kz.zhelezyaka.fitness_server.service;

import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.model.Role;
import kz.zhelezyaka.fitness_server.model.User;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Client createClient(Client client) {
        validateClient(client);

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

        // Шифрование пароля клиента
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client client) {
        return clientRepository.findById(id)
                .map(existing -> {
                    validateClient(client);
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
                                    if (client.getPassword() != null && !client.getPassword().isEmpty()) {
                                        user.setPassword(passwordEncoder.encode(client.getPassword()));
                                    }
                                    userRepository.save(user);
                                });
                    } else if (client.getPassword() != null && !client.getPassword().isEmpty()) {
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
                    if (client.getPassword() != null && !client.getPassword().isEmpty()) {
                        existing.setPassword(passwordEncoder.encode(client.getPassword()));
                    }
                    existing.setSubscription(client.getSubscription());
                    return clientRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + id + " не найден."));
    }

    private void validateClient(Client client) {
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

        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }
        if (client.getPassword().length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов.");
        }
    }
}