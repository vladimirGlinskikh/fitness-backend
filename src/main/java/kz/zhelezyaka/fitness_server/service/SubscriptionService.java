package kz.zhelezyaka.fitness_server.service;

import kz.zhelezyaka.fitness_server.model.Subscription;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления абонементами в приложении фитнес-клуба.
 * <p>
 * Этот класс предоставляет методы для создания и обновления абонементов,
 * включая валидацию данных перед сохранением.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Конструктор для создания экземпляра {@code SubscriptionService}.
     *
     * @param subscriptionRepository репозиторий для работы с абонементами
     */

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Создаёт новый абонемент.
     * <p>
     * Выполняет валидацию данных абонемента перед сохранением.
     * </p>
     *
     * @param subscription объект {@code Subscription} с данными нового абонемента
     * @return созданный объект {@code Subscription}
     * @throws IllegalArgumentException если данные абонемента не прошли валидацию
     */

    public Subscription createSubscription(Subscription subscription) {
        validateSubscription(subscription);
        return subscriptionRepository.save(subscription);
    }

    /**
     * Обновляет данные абонемента по его идентификатору.
     * <p>
     * Выполняет валидацию данных перед обновлением.
     * </p>
     *
     * @param id идентификатор абонемента
     * @param subscription объект {@code Subscription} с обновлёнными данными
     * @return обновлённый объект {@code Subscription}
     * @throws IllegalArgumentException если абонемент с указанным ID не найден или данные не прошли валидацию
     */

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

    /**
     * Проверяет данные абонемента на соответствие правилам.
     * <p>
     * Правила валидации:
     * <ul>
     *     <li>Тип: не пустой, 2–50 символов.</li>
     *     <li>Стоимость: больше 0.</li>
     *     <li>Длительность: больше 0 и не более 365 дней.</li>
     * </ul>
     * Также форматирует тип абонемента, удаляя лишние пробелы.
     * </p>
     *
     * @param subscription объект {@code Subscription} для проверки
     * @throws IllegalArgumentException если данные не соответствуют правилам
     */

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