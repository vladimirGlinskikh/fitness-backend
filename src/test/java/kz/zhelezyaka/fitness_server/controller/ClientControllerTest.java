package kz.zhelezyaka.fitness_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.zhelezyaka.fitness_server.model.Client;
import kz.zhelezyaka.fitness_server.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllClients() throws Exception {
        Client client = new Client();
        client.setId(1L);
        client.setName("Иван Иванов");
        client.setPhone("+79876543210");

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"));
    }

    @Test
    public void testGetClientById() throws Exception {
        Client client = new Client();
        client.setId(1L);
        client.setName("Иван Иванов");
        client.setPhone("+79876543210");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Иванов"));
    }

    @Test
    public void testCreateClient() throws Exception {
        Client client = new Client();
        client.setName("Мария Петрова");
        client.setPhone("+79991234567");

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Мария Петрова"));
    }

    @Test
    public void testCreateClientWithInvalidName() throws Exception {
        Client client = new Client();
        client.setName("Иван123"); // Некорректное имя
        client.setPhone("+79991234567");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest());
    }
}