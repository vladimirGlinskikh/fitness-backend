package kz.zhelezyaka.fitness_server.controller;

import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления клиентами в приложении фитнес-клуба.
 * <p>
 * Этот класс предоставляет REST API эндпоинты для выполнения операций с клиентами,
 * таких как получение списка клиентов, создание, обновление, удаление и получение
 * текущего клиента на основе аутентификации.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    /**
     * Тестовый эндпоинт для проверки работы API.
     *
     * @return строка, подтверждающая, что эндпоинт работает
     */

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }

    /**
     * Возвращает список всех клиентов.
     *
     * @return список объектов {@code Client}
     */

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Возвращает клиента по его идентификатору.
     *
     * @param id идентификатор клиента
     * @return объект {@code ResponseEntity} с клиентом, если найден, или статус 404, если не найден
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
     * @param client объект {@code Client} с данными нового клиента
     * @return созданный объект {@code Client}
     */

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    /**
     * Обновляет данные клиента по его идентификатору.
     *
     * @param id идентификатор клиента
     * @param client объект {@code Client} с обновлёнными данными
     * @return объект {@code ResponseEntity} с обновлённым клиентом или статус 400, если обновление не удалось
     */

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        try {
            Client updatedClient = clientService.updateClient(id, client);
            return ResponseEntity.ok(updatedClient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Удаляет клиента по его идентификатору.
     *
     * @param id идентификатор клиента
     * @return объект {@code ResponseEntity} со статусом 200, если клиент удалён, или 404, если не найден
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
     * Возвращает данные текущего аутентифицированного клиента.
     *
     * @param authentication объект аутентификации, содержащий имя пользователя
     * @return объект {@code ResponseEntity} с данными клиента, если найден, или статус 404, если не найден
     */

    @GetMapping("/me")
    public ResponseEntity<Client> getCurrentClient(Authentication authentication) {
        System.out.println("Processing /api/clients/me for user: " + authentication.getName());
        String username = authentication.getName();
        return clientRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}