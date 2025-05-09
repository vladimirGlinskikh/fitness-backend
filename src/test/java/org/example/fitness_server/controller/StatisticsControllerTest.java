package org.example.fitness_server.controller;

import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.List;

class StatisticsControllerTest {

    @InjectMocks
    private StatisticsController controller;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // инициализация mock объектов
    }

    @Test
    void testGetStatisticsWithNoSubscriptions() {
        // Подготовка фиктивных данных
        given(clientRepository.count()).willReturn(5L); // имитируем наличие 5 клиентов
        given(subscriptionRepository.findAll()).willReturn(List.of()); // нет активных абонементов

        // Выполнение метода контроллера
        var result = controller.getStatistics();

        // Проверка результатов
        assertThat(result.get("totalClients")).isEqualTo(5L);
        assertThat(result.get("totalSubscriptions")).isEqualTo(0L);
        assertThat(result.get("averageSubscriptionCost")).isEqualTo(0.0); // средняя стоимость должна быть равна нулю
    }
}