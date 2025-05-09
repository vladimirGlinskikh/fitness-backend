package org.example.fitness_server.controller;

import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.repository.SubscriptionRepository;
import org.example.fitness_server.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления абонементами в приложении фитнес-клуба.
 * <p>
 * Этот класс предоставляет REST API эндпоинты для выполнения операций с абонементами,
 * таких как получение списка абонементов, создание, обновление и удаление.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Возвращает список всех абонементов.
     *
     * @return список объектов {@code Subscription}
     */

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Возвращает абонемент по его идентификатору.
     *
     * @param id идентификатор абонемента
     * @return объект {@code ResponseEntity} с абонементом, если найден, или статус 404, если не найден
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
     * @param subscription объект {@code Subscription} с данными нового абонемента
     * @return созданный объект {@code Subscription}
     */

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        return subscriptionService.createSubscription(subscription);
    }

    /**
     * Обновляет данные абонемента по его идентификатору.
     *
     * @param id идентификатор абонемента
     * @param subscription объект {@code Subscription} с обновлёнными данными
     * @return объект {@code ResponseEntity} с обновлённым абонементом или статус 400, если обновление не удалось
     */

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscription) {
        try {
            Subscription updated = subscriptionService.updateSubscription(id, subscription);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Удаляет абонемент по его идентификатору.
     *
     * @param id идентификатор абонемента
     * @return объект {@code ResponseEntity} со статусом 200, если абонемент удалён, или 404, если не найден
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