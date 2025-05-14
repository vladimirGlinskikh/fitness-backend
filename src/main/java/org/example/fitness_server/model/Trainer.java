package org.example.fitness_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность тренера фитнес-клуба.
 * <p>
 * Этот класс представляет модель данных для тренеров, хранящихся в базе данных.
 * Таблица в базе данных называется {@code trainers}. Класс реализует интерфейс
 * {@code UserEntity} для унификации работы с пользовательскими данными.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Entity
@Getter
@Setter
@Table(name = "trainers")
public class Trainer implements UserEntity{

    /**
     * Уникальный идентификатор тренера.
     * <p>
     * Генерируется автоматически с использованием стратегии {@code GenerationType.IDENTITY}.
     * </p>
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя тренера.
     * <p>
     * Не может быть {@code null}. Подвергается очистке от лишних пробелов перед сохранением.
     * </p>
     */

    @Column(nullable = false)
    private String name;

    /**
     * Имя пользователя тренера.
     * <p>
     * Должно быть уникальным в таблице {@code trainers} и не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Пароль тренера.
     * <p>
     * Не может быть {@code null}. Хранится в зашифрованном виде.
     * </p>
     */

    @Column(nullable = false)
    private String password;

    /**
     * Список клиентов, связанных с данным тренером.
     * <p>
     * Отношение один-ко-многим с сущностью {@code Client}. Игнорируется при сериализации
     * в JSON благодаря аннотации {@code @JsonIgnore}.
     * </p>
     */

    @OneToMany(mappedBy = "trainer")
    @JsonIgnore
    private List<Client> clients = new ArrayList<>();

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется для работы с JPA при создании экземпляров сущности.
     * </p>
     */

    public Trainer() {
    }
}