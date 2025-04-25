package kz.zhelezyaka.fitness_server.config;

import kz.zhelezyaka.fitness_server.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Клиент может видеть только свои данные
                        .requestMatchers("/api/clients/me").hasAnyRole("ADMIN", "CLIENT")
                        // Разрешить доступ всем к эндпоинту аутентификации
                        .requestMatchers("/api/auth/**").permitAll()
                        // Только ADMIN может управлять пользователями
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Только ADMIN может управлять абонементами
                        .requestMatchers("/api/subscriptions/**").hasRole("ADMIN")
                        // Только ADMIN может управлять всеми клиентами
                        .requestMatchers("/api/clients/**").hasRole("ADMIN")
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("Realm"))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}