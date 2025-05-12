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

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private StatisticsController statisticsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

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