package kz.zhelezyaka.fitness_server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {

    public Subscription() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private double cost;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @OneToMany(mappedBy = "subscription")
    @JsonIgnore
    private List<Client> clients;
}
