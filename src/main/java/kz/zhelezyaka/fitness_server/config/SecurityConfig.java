package kz.zhelezyaka.fitness_server.config;

import kz.zhelezyaka.fitness_server.model.User;
import kz.zhelezyaka.fitness_server.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

/**
 * Конфигурационный класс для настройки безопасности приложения фитнес-клуба.
 * <p>
 * Этот класс настраивает Spring Security для управления аутентификацией и авторизацией.
 * Определяет правила доступа к различным эндпоинтам API, предоставляет сервис для загрузки
 * пользователей и кодировщик паролей.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    /**
     * Конструктор для создания экземпляра {@code SecurityConfig}.
     *
     * @param userRepository репозиторий для работы с пользователями
     */

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Настраивает цепочку фильтров безопасности для HTTP-запросов.
     * <p>
     * Определяет правила авторизации:
     * <ul>
     *     <li>Эндпоинт {@code /api/clients/me} доступен ролям {@code ADMIN} и {@code CLIENT}.</li>
     *     <li>Эндпоинты {@code /api/auth/**} доступны всем (аутентификация).</li>
     *     <li>Эндпоинты {@code /api/users/**}, {@code /api/subscriptions/**}, {@code /api/clients/**}
     *         доступны только роли {@code ADMIN}.</li>
     *     <li>Все остальные запросы требуют аутентификации.</li>
     * </ul>
     * Использует HTTP Basic-аутентификацию и отключает CSRF-защиту.
     *
     * @param http объект {@code HttpSecurity} для настройки безопасности
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка при настройке
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Клиент может видеть только свои данные
                        .requestMatchers("/api/clients/me").hasAnyRole("ADMIN", "CLIENT")
                        // Тренер может видеть свои данные
                        .requestMatchers("/api/trainers/me").hasAnyRole("ADMIN", "TRAINER")
                        // Разрешить доступ всем к эндпоинту аутентификации
                        .requestMatchers("/api/auth/**").permitAll()
                        // Только ADMIN может управлять пользователями
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Только ADMIN может управлять абонементами
                        .requestMatchers("/api/subscriptions/**").hasRole("ADMIN")
                        // Только ADMIN может управлять всеми клиентами
                        .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "TRAINER")
                        // Только ADMIN может управлять тренерами
                        .requestMatchers("/api/trainers/**").hasRole("ADMIN")
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("Realm"))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Создаёт сервис для загрузки данных пользователя по имени пользователя.
     * <p>
     * Использует {@code UserRepository} для поиска пользователя по имени.
     * Если пользователь не найден, выбрасывается исключение {@code UsernameNotFoundException}.
     * </p>
     *
     * @return сервис для загрузки данных пользователя
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        };
    }
    /**
     * Создаёт кодировщик паролей на основе алгоритма BCrypt.
     *
     * @return кодировщик паролей
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}