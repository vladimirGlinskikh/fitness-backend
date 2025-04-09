package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
