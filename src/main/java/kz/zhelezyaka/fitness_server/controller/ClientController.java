package kz.zhelezyaka.fitness_server.controller;

import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * REST-контроллер для управления клиентами.
 * Предоставляет эндпоинты для выполнения CRUD-операций с клиентами.
 */

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;

    /**
     * Получает список всех клиентов.
     *
     * @return Список клиентов
     */
    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Получает клиента по его ID.
     *
     * @param id ID клиента
     * @return ResponseEntity с клиентом или статус 404, если клиент не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создаёт нового клиента.
     *
     * @param client Клиент для создания
     * @return Созданный клиент
     * @throws IllegalArgumentException Если данные клиента некорректны
     */
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        validateClient(client);
        return clientRepository.save(client);
    }

    /**
     * Обновляет существующего клиента.
     *
     * @param id     ID клиента для обновления
     * @param client Обновлённые данные клиента
     * @return ResponseEntity с обновлённым клиентом или статус 404, если клиент не найден
     * @throws IllegalArgumentException Если данные клиента некорректны
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        return clientRepository.findById(id)
                .map(existing -> {
                    client.setId(id);
                    validateClient(client);
                    return ResponseEntity.ok(clientRepository.save(client));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаляет клиента по его ID.
     *
     * @param id ID клиента для удаления
     * @return ResponseEntity с статусом 200, если удаление успешно, или 404, если клиент не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Проверяет корректность данных клиента.
     *
     * @param client Клиент для проверки
     * @throws IllegalArgumentException Если имя или номер телефона не соответствуют формату
     */
    private void validateClient(Client client) {
        // Валидация имени
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

        client.setName(cleanedName); // Сохраняем отформатированное имя

        // Валидация номера телефона
        if (client.getPhone() == null || !Pattern.matches("\\+7\\d{10}", client.getPhone())) {
            throw new IllegalArgumentException("Номер телефона должен начинаться с +7 и содержать 10 цифр (например, +79876543210).");
        }
    }
}
