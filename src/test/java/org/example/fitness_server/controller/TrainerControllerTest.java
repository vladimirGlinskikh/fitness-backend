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

/**
 * Тестовый класс для контроллера {@code TrainerController}.
 * <p>
 * Этот класс содержит юнит-тесты для проверки функциональности эндпоинтов
 * контроллера {@code TrainerController}, включая получение списка тренеров,
 * получение тренера по ID, создание, обновление, удаление и получение текущего
 * тренера. Использует Mockito для мок-объектов и Spring Test для симуляции
 * HTTP-запросов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

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

    /**
     * Инициализирует тестовую среду перед каждым тестом.
     * <p>
     * Настраивает {@code MockMvc} для выполнения HTTP-запросов, создаёт
     * экземпляр {@code ObjectMapper} для сериализации/десериализации JSON,
     * и инициализирует тестовый объект {@code Trainer} с предопределёнными данными.
     * </p>
     */

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

    /**
     * Тестирует эндпоинт {@code GET /api/trainers}.
     * <p>
     * Проверяет, что возвращается список тренеров с корректными данными
     * и что вызывается метод репозитория для получения всех тренеров.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getAllTrainers_ReturnsTrainers() throws Exception {
        when(trainerRepository.findAll()).thenReturn(Collections.singletonList(trainer));

        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Тренер Иванов")));

        verify(trainerRepository).findAll();
    }

    /**
     * Тестирует эндпоинт {@code GET /api/trainers/{id}} при наличии тренера.
     * <p>
     * Проверяет, что возвращается тренер с корректными данными
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getTrainerById_TrainerExists_ReturnsTrainer() throws Exception {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Тренер Иванов")));

        verify(trainerRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/trainers/{id}} при отсутствии тренера.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getTrainerById_TrainerNotFound_Returns404() throws Exception {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainers/1"))
                .andExpect(status().isNotFound());

        verify(trainerRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code POST /api/trainers} с валидным тренером.
     * <p>
     * Проверяет, что возвращается созданный тренер
     * и что вызывается метод сервиса для создания.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

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

    /**
     * Тестирует эндпоинт {@code PUT /api/trainers/{id}} при наличии тренера.
     * <p>
     * Проверяет, что возвращается обновлённый тренер
     * и что вызывается метод сервиса для обновления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

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

    /**
     * Тестирует эндпоинт {@code PUT /api/trainers/{id}} при отсутствии тренера.
     * <p>
     * Проверяет, что возвращается статус 400
     * и что вызывается метод сервиса для обновления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

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

    /**
     * Тестирует эндпоинт {@code DELETE /api/trainers/{id}} при наличии тренера.
     * <p>
     * Проверяет, что возвращается статус 200
     * и что вызывается метод удаления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteTrainer_TrainerExists_Returns200() throws Exception {
        when(trainerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isOk());

        verify(trainerRepository).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/trainers/{id}} при отсутствии тренера.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что метод удаления не вызывается.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteTrainer_TrainerNotFound_Returns404() throws Exception {
        when(trainerRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/trainers/1"))
                .andExpect(status().isNotFound());

        verify(trainerRepository, never()).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/trainers/me} при наличии тренера.
     * <p>
     * Проверяет, что возвращается текущий тренер с корректными данными
     * и что вызывается метод поиска по имени пользователя.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

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

    /**
     * Тестирует эндпоинт {@code GET /api/trainers/me} при отсутствии тренера.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что вызывается метод поиска по имени пользователя.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

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