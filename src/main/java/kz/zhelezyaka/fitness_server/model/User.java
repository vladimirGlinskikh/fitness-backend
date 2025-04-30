package kz.zhelezyaka.fitness_server.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Сущность, представляющая пользователя в приложении фитнес-клуба.
 * <p>
 * Этот класс является JPA-сущностью, которая отображается на таблицу {@code users} в базе данных.
 * Реализует интерфейс {@code UserDetails} для интеграции с Spring Security, предоставляя информацию
 * об имени пользователя, пароле, роли и статусе учётной записи.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется для JPA при создании экземпляров сущности.
     */
    public User() {
    }

    /**
     * Уникальный идентификатор пользователя.
     * <p>
     * Генерируется автоматически с использованием стратегии {@code GenerationType.IDENTITY}.
     * </p>
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     * <p>
     * Должно быть уникальным в таблице {@code users} и не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Пароль пользователя.
     * <p>
     * Не может быть {@code null}.
     * </p>
     */

    @Column(nullable = false)
    private String password;

    /**
     * Роль пользователя.
     * <p>
     * Определяет уровень доступа (например, {@code ADMIN} или {@code CLIENT}).
     * Хранится как строка и не может быть {@code null}.
     * </p>
     */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Возвращает роли пользователя для Spring Security.
     * <p>
     * Создаёт единственную роль на основе значения {@code role}, добавляя префикс "ROLE_".
     * Например, для роли {@code ADMIN} возвращается {@code ROLE_ADMIN}.
     * </p>
     *
     * @return коллекция ролей пользователя
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Проверяет, не истёк ли срок действия учётной записи.
     *
     * @return {@code true}, так как срок действия учётной записи не ограничен
     */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, не заблокирована ли учётная запись.
     *
     * @return {@code true}, так как учётная запись не заблокирована
     */

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, не истёк ли срок действия учётных данных.
     *
     * @return {@code true}, так как срок действия учётных данных не ограничен
     */

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активна ли учётная запись.
     *
     * @return {@code true}, так как учётная запись всегда активна
     */

    @Override
    public boolean isEnabled() {
        return true;
    }
}