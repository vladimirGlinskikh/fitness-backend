package kz.zhelezyaka.fitness_server.repository;

import kz.zhelezyaka.fitness_server.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
