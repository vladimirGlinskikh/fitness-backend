package org.example.fitness_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Сущность, представляющая абонемент фитнес-клуба.
 * <p>
 * Этот класс является JPA-сущностью, которая отображается на таблицу {@code subscriptions} в базе данных.
 * Содержит информацию об абонементе, такую как тип, стоимость, длительность и список связанных клиентов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется для JPA при создании экземпляров сущности.
     * </p>
     */

    public Subscription() {
    }

    /**
     * Уникальный идентификатор абонемента.
     * <p>
     * Генерируется автоматически с использованием стратегии {@code GenerationType.IDENTITY}.
     * </p>
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип абонемента.
     * <p>
     * Например, "Месячный" или "Годовой". Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private String type;

    /**
     * Стоимость абонемента.
     * <p>
     * Указывается в денежных единицах (например, рублях). Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private double cost;

    /**
     * Длительность абонемента в днях.
     * <p>
     * Например, 30 для месячного абонемента. Не может быть {@code null}.
     * </p>
     */

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    /**
     * Список клиентов, использующих этот абонемент.
     * <p>
     * Связь типа "один-ко-многим" с сущностью {@code Client}. Игнорируется при сериализации в JSON
     * с помощью аннотации {@code @JsonIgnore}.
     * </p>
     */

    @OneToMany(mappedBy = "subscription")
    @JsonIgnore
    private List<Client> clients;
}
