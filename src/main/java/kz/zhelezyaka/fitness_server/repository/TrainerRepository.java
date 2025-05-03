package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
}