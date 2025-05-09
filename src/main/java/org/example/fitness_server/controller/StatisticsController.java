package org.example.fitness_server.controller;

import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST-контроллер для получения статистики приложения фитнес-клуба.
 * <p>
 * Этот класс предоставляет эндпоинт для получения статистических данных,
 * таких как общее количество клиентов, абонементов и средняя стоимость абонемента.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Получает статистические данные о клиентах и абонементах.
     * <p>
     * Возвращает следующие показатели:
     * <ul>
     *     <li>{@code totalClients} — общее количество клиентов.</li>
     *     <li>{@code totalSubscriptions} — общее количество абонементов.</li>
     *     <li>{@code averageSubscriptionCost} — средняя стоимость абонемента (0.0, если абонементов нет).</li>
     * </ul>
     *
     * @return объект {@code Map} с данными: общее количество клиентов, абонементов и средняя стоимость абонемента
     */

    @GetMapping
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Общее количество клиентов
        long totalClients = clientRepository.count();
        statistics.put("totalClients", totalClients);

        // Общее количество абонементов
        long totalSubscriptions = subscriptionRepository.count();
        statistics.put("totalSubscriptions", totalSubscriptions);

        // Средняя стоимость абонемента
        Double averageCost = subscriptionRepository.findAll().stream()
                .mapToDouble(Subscription::getCost)
                .average()
                .orElse(0.0);
        statistics.put("averageSubscriptionCost", averageCost);

        return statistics;
    }
}
