package kz.zhelezyaka.fitness_server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность, представляющая клиента фитнес-клуба.
 * <p>
 * Этот класс является JPA-сущностью, которая отображается на таблицу {@code clients} в базе данных.
 * Содержит информацию о клиенте, такую как имя, телефон, имя пользователя, пароль и связанный абонемент.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client {

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется для JPA при создании экземпляров сущности.
     */
    public Client() {
    }

    /**
     * Уникальный идентификатор клиента.
     * <p>
     * Генерируется автоматически с использованием стратегии {@code GenerationType.IDENTITY}.
     * </p>
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя клиента.
     * <p>
     * Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private String name;

    /**
     * Телефон клиента.
     * <p>
     * Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private String phone;

    /**
     * Имя пользователя клиента.
     * <p>
     * Должно быть уникальным в таблице {@code clients}.
     * </p>
     */

    @Column(unique = true)
    private String username;

    /**
     * Пароль клиента.
     * <p>
     * Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private String password;

    /**
     * Абонемент, связанный с клиентом.
     * <p>
     * Связь типа "многие-к-одному" с сущностью {@code Subscription}.
     * </p>
     */

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
}
