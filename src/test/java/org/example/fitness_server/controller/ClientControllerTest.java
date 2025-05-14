package org.example.fitness_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitness_server.model.Client;
import org.example.fitness_server.model.Subscription;
import org.example.fitness_server.model.Trainer;
import org.example.fitness_server.repository.ClientRepository;
import org.example.fitness_server.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тестовый класс для контроллера {@code ClientController}.
 * <p>
 * Этот класс содержит юнит-тесты для проверки функциональности эндпоинтов
 * контроллера {@code ClientController}, включая получение списка клиентов,
 * подсчёт количества, получение клиента по ID, создание, обновление, удаление
 * и получение текущего клиента. Использует Mockito для мок-объектов и
 * Spring Test для симуляции HTTP-запросов.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Client client;
    private Subscription subscription;

    /**
     * Инициализирует тестовую среду перед каждым тестом.
     * <p>
     * Настраивает {@code MockMvc}, создаёт экземпляр {@code ObjectMapper},
     * и инициализирует тестовые объекты {@code Client} и {@code Subscription}
     * с предопределёнными данными для использования в тестах.
     * </p>
     */

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
        objectMapper = new ObjectMapper();

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setType("Месячный");
        subscription.setCost(5000.0);
        subscription.setDurationDays(30);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setName("Тренер Иванов");
        trainer.setUsername("trainer1");
        trainer.setPassword("encodedPassword");

        client = new Client();
        client.setId(1L);
        client.setName("Иван Иванов");
        client.setPhone("+79876543210");
        client.setUsername("ivan");
        client.setPassword("encodedPassword");
        client.setSubscription(subscription);
        client.setTrainer(trainer);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients} с пагинацией.
     * <p>
     * Проверяет, что возвращается страница клиентов с корректными данными
     * и что вызывается метод репозитория с правильными параметрами.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getAllClients_ReturnsPagedClients() throws Exception {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Client> clientPage = new PageImpl<>(Collections.singletonList(client), pageable, 1);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);

        mockMvc.perform(get("/api/clients")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Иван Иванов")))
                .andExpect(jsonPath("$.content[0].username", is("ivan")));

        verify(clientRepository).findAll(pageable);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients/count}.
     * <p>
     * Проверяет, что возвращается корректное количество клиентов
     * и что вызывается метод подсчёта репозитория.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getClientCount_ReturnsCount() throws Exception {
        when(clientRepository.count()).thenReturn(5L);

        mockMvc.perform(get("/api/clients/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(clientRepository).count();
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients/{id}} при наличии клиента.
     * <p>
     * Проверяет, что возвращается клиент с корректными данными
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getClientById_ClientExists_ReturnsClient() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")))
                .andExpect(jsonPath("$.username", is("ivan")));

        verify(clientRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients/{id}} при отсутствии клиента.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что вызывается метод поиска по ID.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getClientById_ClientNotFound_Returns404() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isNotFound());

        verify(clientRepository).findById(1L);
    }

    /**
     * Тестирует эндпоинт {@code POST /api/clients} с валидным клиентом.
     * <p>
     * Проверяет, что возвращается созданный клиент
     * и что вызывается метод сервиса.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void createClient_ValidClient_ReturnsCreatedClient() throws Exception {
        Client newClient = new Client();
        newClient.setName("Мария Петрова");
        newClient.setPhone("+79991234567");
        newClient.setUsername("maria");
        newClient.setPassword("maria123");
        newClient.setSubscription(subscription);

        when(clientService.createClient(any(Client.class), any())).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")));

        verify(clientService).createClient(any(Client.class), any());
    }

    /**
     * Тестирует эндпоинт {@code PUT /api/clients/{id}} при наличии клиента.
     * <p>
     * Проверяет, что возвращается обновлённый клиент
     * и что вызывается метод сервиса.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void updateClient_ClientExists_ReturnsUpdatedClient() throws Exception {
        Client updatedClient = new Client();
        updatedClient.setName("Иван Петров");
        updatedClient.setPhone("+79991234567");
        updatedClient.setUsername("ivan");
        updatedClient.setPassword("newPassword");
        updatedClient.setSubscription(subscription);

        when(clientService.updateClient(eq(1L), any(Client.class), any())).thenReturn(client);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")));

        verify(clientService).updateClient(eq(1L), any(Client.class), any());
    }

    /**
     * Тестирует эндпоинт {@code PUT /api/clients/{id}} при отсутствии клиента.
     * <p>
     * Проверяет, что возвращается статус 400
     * и что вызывается метод сервиса.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void updateClient_ClientNotFound_Returns400() throws Exception {
        Client updatedClient = new Client();
        updatedClient.setName("Иван Петров");
        updatedClient.setPhone("+79991234567");
        updatedClient.setUsername("ivan");
        updatedClient.setPassword("newPassword");
        updatedClient.setSubscription(subscription);

        when(clientService.updateClient(eq(1L), any(Client.class), any()))
                .thenThrow(new IllegalArgumentException("Клиент с ID 1 не найден."));

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isBadRequest());

        verify(clientService).updateClient(eq(1L), any(Client.class), any());
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/clients/{id}} при наличии клиента.
     * <p>
     * Проверяет, что возвращается статус 200
     * и что вызывается метод удаления.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteClient_ClientExists_Returns200() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clients/1")
                        .principal(auth))
                .andExpect(status().isOk());

        verify(clientRepository).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/clients/{id}} при отсутствии клиента.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что метод удаления не вызывается.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteClient_ClientNotFound_Returns404() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/clients/1")
                        .principal(auth))
                .andExpect(status().isNotFound());

        verify(clientRepository, never()).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code DELETE /api/clients/{id}} при попытке самоудаления.
     * <p>
     * Проверяет, что возвращается статус 403
     * и что метод удаления не вызывается.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void deleteClient_SelfDeletion_Returns403() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("ivan");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        mockMvc.perform(delete("/api/clients/1")
                        .principal(auth))
                .andExpect(status().isForbidden());

        verify(clientRepository, never()).deleteById(1L);
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients/me} при наличии клиента.
     * <p>
     * Проверяет, что возвращается текущий клиент с корректными данными
     * и что вызывается метод поиска по имени пользователя.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getCurrentClient_ClientExists_ReturnsClient() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("ivan");
        when(clientRepository.findByUsername("ivan")).thenReturn(Optional.of(client));

        mockMvc.perform(get("/api/clients/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")));

        verify(clientRepository).findByUsername("ivan");
    }

    /**
     * Тестирует эндпоинт {@code GET /api/clients/me} при отсутствии клиента.
     * <p>
     * Проверяет, что возвращается статус 404
     * и что вызывается метод поиска по имени пользователя.
     * </p>
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */

    @Test
    void getCurrentClient_ClientNotFound_Returns404() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("ivan");
        when(clientRepository.findByUsername("ivan")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/me")
                        .principal(auth))
                .andExpect(status().isNotFound());

        verify(clientRepository).findByUsername("ivan");
    }
}