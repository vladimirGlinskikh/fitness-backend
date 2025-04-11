package kz.zhelezyaka.fitness_server.controller;

import kz.zhelezyaka.fitness_server.model.Subscription;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления абонементами.
 * Предоставляет эндпоинты для выполнения CRUD-операций с абонементами.
 */

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Получает список всех абонементов.
     *
     * @return Список абонементов
     */

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Получает абонемент по его ID.
     *
     * @param id ID абонемента
     * @return ResponseEntity с абонементом или статус 404, если абонемент не найден
     */

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создаёт новый абонемент.
     *
     * @param subscription Абонемент для создания
     * @return Созданный абонемент
     */

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    /**
     * Обновляет существующий абонемент.
     *
     * @param id           ID абонемента для обновления
     * @param subscription Обновлённые данные абонемента
     * @return ResponseEntity с обновлённым абонементом или статус 404, если абонемент не найден
     */

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscription) {
        return subscriptionRepository.findById(id)
                .map(existing -> {
                    subscription.setId(id);
                    return ResponseEntity.ok(subscriptionRepository.save(subscription));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаляет абонемент по его ID.
     *
     * @param id ID абонемента для удаления
     * @return ResponseEntity с статусом 200, если удаление успешно, или 404, если абонемент не найден
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
