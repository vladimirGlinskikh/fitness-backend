package kz.zhelezyaka.fitness_server.controller;

import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;

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
                .mapToDouble(sub -> sub.getCost())
                .average()
                .orElse(0.0);
        statistics.put("averageSubscriptionCost", averageCost);

        return statistics;
    }
}
