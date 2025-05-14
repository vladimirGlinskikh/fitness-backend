package org.example.fitness_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.repository.SubscriptionRepository;
import org.example.fitness_server.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тестовый класс для контроллера {@code SubscriptionController}.
 * <p>
 * Этот класс содержит юнит-тесты для проверки функциональности эндпоинтов
 * контроллера {@code SubscriptionController}, включая получение списка абонементов,
 * получение абонемента по ID, создание, обновление и удаление абонемента.
 * Использует Mockito для мок-объектов и Spring Test для симуляции HTTP-запросов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Subscription subscription;

    /**
     * Инициализирует тестовую среду перед каждым тестом.
     * <p>
     * Настраивает {@code MockMvc} для выполнения HTTP-запросов, создаёт
     * экземпляр {@code ObjectMapper} для сериализации/десериализации JSON,
     * и инициализирует тестовый объект {@code Subscription} с предопределёнными данными.
     * </p>
     */

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
        objectMapper = new ObjectMapper();

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setType("Месячный");
        subscription.setCost(5000.0);
        subscription.setDurationDays(30);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/subscriptions}.
     * <p>
     * Проверяет, что возвращается список абонементов с корректными данными
     * и что вызывается метод репозитория для получения всех абонементов.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getAllSubscriptions_ReturnsSubscriptions() throws Exception {
        when(subscriptionRepository.findAll()).thenReturn(Collections.singletonList(subscription));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type", is("Месячный")));

        verify(subscriptionRepository).findAll();
    }

    /**
     * Тестирует эндпоинт {@code GET /api/subscriptions/{id}} при наличии абонемента.
     * <p>
     * Проверяет, что возвращается абонемент с корректными данными
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getSubscriptionById_SubscriptionExists_ReturnsSubscription() throws Exception {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.type", is("Месячный")));

        verify(subscriptionRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/subscriptions/{id}} при отсутствии абонемента.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getSubscriptionById_SubscriptionNotFound_Returns404() throws Exception {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code POST /api/subscriptions} с валидным абонементом.
     * <p>
     * Проверяет, что возвращается созданный абонемент
     * и что вызывается метод сервиса для создания.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void createSubscription_ValidSubscription_ReturnsCreatedSubscription() throws Exception {
        Subscription newSubscription = new Subscription();
        newSubscription.setType("Годовой");
        newSubscription.setCost(45000.0);
        newSubscription.setDurationDays(365);

        when(subscriptionService.createSubscription(any(Subscription.class))).thenReturn(subscription);

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSubscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionService).createSubscription(any(Subscription.class));
    }

    /**
     * Тестирует эндпоинт {@code PUT /api/subscriptions/{id}} при наличии абонемента.
     * <p>
     * Проверяет, что возвращается обновлённый абонемент
     * и что вызывается метод сервиса для обновления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void updateSubscription_SubscriptionExists_ReturnsUpdatedSubscription() throws Exception {
        Subscription updatedSubscription = new Subscription();
        updatedSubscription.setType("Годовой");
        updatedSubscription.setCost(45000.0);
        updatedSubscription.setDurationDays(365);

        when(subscriptionService.updateSubscription(eq(1L), any(Subscription.class))).thenReturn(subscription);

        mockMvc.perform(put("/api/subscriptions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSubscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionService).updateSubscription(eq(1L), any(Subscription.class));
    }

    /**
     * Тестирует эндпоинт {@code PUT /api/subscriptions/{id}} при отсутствии абонемента.
     * <p>
     * Проверяет, что возвращается статус 400
     * и что вызывается метод сервиса для обновления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void updateSubscription_SubscriptionNotFound_Returns400() throws Exception {
        Subscription updatedSubscription = new Subscription();
        updatedSubscription.setType("Годовой");
        updatedSubscription.setCost(45000.0);
        updatedSubscription.setDurationDays(365);

        when(subscriptionService.updateSubscription(eq(1L), any(Subscription.class)))
                .thenThrow(new IllegalArgumentException("Абонемент с ID 1 не найден."));

        mockMvc.perform(put("/api/subscriptions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSubscription)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService).updateSubscription(eq(1L), any(Subscription.class));
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/subscriptions/{id}} при наличии абонемента.
     * <p>
     * Проверяет, что возвращается статус 200
     * и что вызывается метод удаления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteSubscription_SubscriptionExists_Returns200() throws Exception {
        when(subscriptionRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/subscriptions/1"))
                .andExpect(status().isOk());

        verify(subscriptionRepository).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/subscriptions/{id}} при отсутствии абонемента.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что метод удаления не вызывается.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteSubscription_SubscriptionNotFound_Returns404() throws Exception {
        when(subscriptionRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/subscriptions/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionRepository, never()).deleteById(1L);
    }
}