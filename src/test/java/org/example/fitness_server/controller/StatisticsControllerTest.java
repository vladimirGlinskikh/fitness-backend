package org.example.fitness_server.controller;

import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тестовый класс для контроллера {@code StatisticsController}.
 * <p>
 * Этот класс содержит юнит-тесты для проверки функциональности эндпоинта
 * {@code StatisticsController}, который предоставляет статистику по клиентам
 * и абонементам. Тесты проверяют корректность подсчёта клиентов, абонементов
 * и средней стоимости абонемента. Использует Mockito для мок-объектов и
 * Spring Test для симуляции HTTP-запросов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private StatisticsController statisticsController;

    private MockMvc mockMvc;

    /**
     * Инициализирует тестовую среду перед каждым тестом.
     * <p>
     * Настраивает {@code MockMvc} для выполнения HTTP-запросов к контроллеру
     * {@code StatisticsController}.
     * </p>
     */

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    /**
     * Тестирует эндпоинт {@code GET /api/statistics} с имеющимися абонементами.
     * <p>
     * Проверяет, что возвращается корректная статистика, включая общее количество
     * клиентов, количество абонементов и среднюю стоимость абонемента.
     * Также проверяет, что вызываются методы репозиториев для получения данных.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getStatistics_ReturnsStatistics() throws Exception {
        Subscription subscription1 = new Subscription();
        subscription1.setCost(5000.0);

        Subscription subscription2 = new Subscription();
        subscription2.setCost(45000.0);

        when(clientRepository.count()).thenReturn(5L);
        when(subscriptionRepository.count()).thenReturn(2L);
        when(subscriptionRepository.findAll()).thenReturn(Arrays.asList(subscription1, subscription2));

        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClients", is(5)))
                .andExpect(jsonPath("$.totalSubscriptions", is(2)))
                .andExpect(jsonPath("$.averageSubscriptionCost", is(25000.0)));

        verify(clientRepository).count();
        verify(subscriptionRepository).count();
        verify(subscriptionRepository).findAll();
    }

    /**
     * Тестирует эндпоинт {@code GET /api/statistics} при отсутствии абонементов.
     * <p>
     * Проверяет, что возвращается корректная статистика с нулевой средней стоимостью
     * абонемента, а также корректное количество клиентов и абонементов.
     * Убеждается, что вызываются методы репозиториев для получения данных.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getStatistics_NoSubscriptions_ReturnsZeroAverageCost() throws Exception {
        when(clientRepository.count()).thenReturn(5L);
        when(subscriptionRepository.count()).thenReturn(0L);
        when(subscriptionRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClients", is(5)))
                .andExpect(jsonPath("$.totalSubscriptions", is(0)))
                .andExpect(jsonPath("$.averageSubscriptionCost", is(0.0)));

        verify(clientRepository).count();
        verify(subscriptionRepository).count();
        verify(subscriptionRepository).findAll();
    }
}