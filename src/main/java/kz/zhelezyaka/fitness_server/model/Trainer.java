package kz.zhelezyaka.fitness_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность тренера фитнес-клуба.
 */
@Entity
@Getter
@Setter
@Table(name = "trainers")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "trainer")
    @JsonIgnore
    private List<Client> clients = new ArrayList<>();

    /**
     * Конструктор по умолчанию.
     */
    public Trainer() {
    }
}