package kz.zhelezyaka.fitness_server.service;

import kz.zhelezyaka.fitness_server.model.Subscription;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createSubscription(Subscription subscription) {
        validateSubscription(subscription);
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Long id, Subscription subscription) {
        return subscriptionRepository.findById(id)
                .map(existing -> {
                    validateSubscription(subscription);
                    existing.setType(subscription.getType());
                    existing.setCost(subscription.getCost());
                    existing.setDurationDays(subscription.getDurationDays());
                    return subscriptionRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Абонемент с ID " + id + " не найден."));
    }

    private void validateSubscription(Subscription subscription) {
        if (subscription.getType() == null || subscription.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Тип абонемента не может быть пустым.");
        }
        String cleanedType = subscription.getType().trim();
        if (cleanedType.length() < 2 || cleanedType.length() > 50) {
            throw new IllegalArgumentException("Тип абонемента должен содержать от 2 до 50 символов.");
        }
        subscription.setType(cleanedType);

        if (subscription.getCost() <= 0) {
            throw new IllegalArgumentException("Стоимость абонемента должна быть больше 0.");
        }

        if (subscription.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Длительность абонемента должна быть больше 0 дней.");
        }
        if (subscription.getDurationDays() > 365) {
            throw new IllegalArgumentException("Длительность абонемента не может превышать 365 дней.");
        }
    }
}