package org.example.fitness_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitness_server.model.Trainer;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
class TrainerControllerTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
        objectMapper = new ObjectMapper();

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Тренер Иванов");
        trainer.setUsername("trainer1");
        trainer.setPassword("encodedPassword");
    }

    @Test
    void getAllTrainers_ReturnsTrainers() throws Exception {
        when(trainerRepository.findAll()).thenReturn(Collections.singletonList(trainer));

        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Тренер Иванов")));

        verify(trainerRepository).findAll();
    }

    @Test
    void getTrainerById_TrainerExists_ReturnsTrainer() throws Exception {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Тренер Иванов")));

        verify(trainerRepository).findById(1L);
    }

    @Test
    void getTrainerById_TrainerNotFound_Returns404() throws Exception {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isNotFound());

        verify(trainerRepository).findById(1L);
    }

    @Test
    void createTrainer_ValidTrainer_ReturnsCreatedTrainer() throws Exception {
        Trainer newTrainer = new Trainer();
        newTrainer.setName("Тренер Петров");
        newTrainer.setUsername("trainer2");
        newTrainer.setPassword("trainer123");

        when(trainerService.createTrainer(any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(trainerService).createTrainer(any(Trainer.class));
    }

    @Test
    void updateTrainer_TrainerExists_ReturnsUpdatedTrainer() throws Exception {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setName("Тренер Сидоров");
        updatedTrainer.setUsername("trainer1");
        updatedTrainer.setPassword("newPassword");

        when(trainerService.updateTrainer(eq(1L), any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(put("/api/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTrainer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(trainerService).updateTrainer(eq(1L), any(Trainer.class));
    }

    @Test
    void updateTrainer_TrainerNotFound_Returns400() throws Exception {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setName("Тренер Сидоров");
        updatedTrainer.setUsername("trainer1");
        updatedTrainer.setPassword("newPassword");

        when(trainerService.updateTrainer(eq(1L), any(Trainer.class)))
                .thenThrow(new IllegalArgumentException("Тренер с ID 1 не найден."));

        mockMvc.perform(put("/api/trainers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTrainer)))
                .andExpect(status().isBadRequest());

        verify(trainerService).updateTrainer(eq(1L), any(Trainer.class));
    }

    @Test
    void deleteTrainer_TrainerExists_Returns200() throws Exception {
        when(trainerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isOk());

        verify(trainerRepository).deleteById(1L);
    }

    @Test
    void deleteTrainer_TrainerNotFound_Returns404() throws Exception {
        when(trainerRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isNotFound());

        verify(trainerRepository, never()).deleteById(1L);
    }

    @Test
    void getCurrentTrainer_TrainerExists_ReturnsTrainer() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("trainer1");
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/api/trainers/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Тренер Иванов")));

        verify(trainerRepository).findByUsername("trainer1");
    }

    @Test
    void getCurrentTrainer_TrainerNotFound_Returns404() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("trainer1");
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainers/me")
                        .principal(auth))
                .andExpect(status().isNotFound());

        verify(trainerRepository).findByUsername("trainer1");
    }
}