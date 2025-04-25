package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsername(String username);
}
