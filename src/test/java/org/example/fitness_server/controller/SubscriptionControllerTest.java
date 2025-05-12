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

    @Test
    void getAllSubscriptions_ReturnsSubscriptions() throws Exception {
        when(subscriptionRepository.findAll()).thenReturn(Collections.singletonList(subscription));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type", is("Месячный")));

        verify(subscriptionRepository).findAll();
    }

    @Test
    void getSubscriptionById_SubscriptionExists_ReturnsSubscription() throws Exception {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.type", is("Месячный")));

        verify(subscriptionRepository).findById(1L);
    }

    @Test
    void getSubscriptionById_SubscriptionNotFound_Returns404() throws Exception {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionRepository).findById(1L);
    }

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

    @Test
    void deleteSubscription_SubscriptionExists_Returns200() throws Exception {
        when(subscriptionRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/subscriptions/1"))
                .andExpect(status().isOk());

        verify(subscriptionRepository).deleteById(1L);
    }

    @Test
    void deleteSubscription_SubscriptionNotFound_Returns404() throws Exception {
        when(subscriptionRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/subscriptions/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionRepository, never()).deleteById(1L);
    }
}