package kz.zhelezyaka.fitness_server.controller;

import kz.zhelezyaka.fitness_server.model.Subscription;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import kz.zhelezyaka.fitness_server.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @Test
    public void testGetStatistics() throws Exception {
        // Мокируем данные
        when(clientRepository.count()).thenReturn(3L);

        Subscription sub1 = new Subscription();
        sub1.setCost(5000.0);
        Subscription sub2 = new Subscription();
        sub2.setCost(10000.0);
        when(subscriptionRepository.findAll()).thenReturn(Arrays.asList(sub1, sub2));
        when(subscriptionRepository.count()).thenReturn(2L);

        // Ожидаемая средняя стоимость: (5000 + 10000) / 2 = 7500
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClients").value(3))
                .andExpect(jsonPath("$.totalSubscriptions").value(2))
                .andExpect(jsonPath("$.averageSubscriptionCost").value(7500.0));
    }
}