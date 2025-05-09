//package kz.zhelezyaka.fitness_server.controller;
//
//import kz.zhelezyaka.fitness_server.exception.GlobalExceptionHandler;
//import kz.zhelezyaka.fitness_server.model.Client;
//import kz.zhelezyaka.fitness_server.model.Subscription;
//import kz.zhelezyaka.fitness_server.repository.ClientRepository;
//import kz.zhelezyaka.fitness_server.service.ClientService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class ClientControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private ClientRepository clientRepository;
//
//    @Mock
//    private ClientService clientService;
//
//    @InjectMocks
//    private ClientController clientController;
//
//    private Client client;
//
//    @BeforeEach
//    void setUp() {
//        // Инициализация MockMvc с контроллером и обработчиком исключений
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(clientController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
//
//        Subscription subscription = new Subscription();
//        subscription.setId(1L);
//        subscription.setType("Месячный");
//        subscription.setCost(5000.00);
//        subscription.setDurationDays(30);
//
//        client = new Client();
//        client.setId(1L);
//        client.setName("Иван Иванов");
//        client.setPhone("+79876543210");
//        client.setUsername("ivan");
//        client.setPassword("encodedPassword");
//        client.setSubscription(subscription);
//    }
//
//    @Test
//    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
//        mockMvc.perform(get("/api/clients/test"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Test endpoint is working!"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getAllClients_ShouldReturnListOfClients() throws Exception {
//        when(clientRepository.findAll()).thenReturn(Collections.singletonList(client));
//
//        mockMvc.perform(get("/api/clients"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
//                .andExpect(jsonPath("$[0].username").value("ivan"));
//
//        verify(clientRepository, times(1)).findAll();
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getClientById_WhenClientExists_ShouldReturnClient() throws Exception {
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//
//        mockMvc.perform(get("/api/clients/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Иван Иванов"));
//
//        verify(clientRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getClientById_WhenClientDoesNotExist_ShouldReturnNotFound() throws Exception {
//        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/clients/1"))
//                .andExpect(status().isNotFound());
//
//        verify(clientRepository, times(1)).findById(1L);
//    }
//
////    @Test
////    @WithMockUser(roles = "ADMIN")
////    void createClient_ShouldReturnCreatedClient() throws Exception {
////        when(clientService.createClient(any(Client.class))).thenReturn(client);
////
////        String clientJson = "{\"name\":\"Иван Иванов\",\"phone\":\"+79876543210\",\"username\":\"ivan\",\"password\":\"ivan123\",\"subscription\":{\"id\":1}}";
////
////        mockMvc.perform(post("/api/clients")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(clientJson))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.id").value(1L))
////                .andExpect(jsonPath("$.name").value("Иван Иванов"));
////
////        verify(clientService, times(1)).createClient(any(Client.class));
////    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateClient_WhenClientExists_ShouldReturnUpdatedClient() throws Exception {
//        when(clientService.updateClient(eq(1L), any(Client.class))).thenReturn(client);
//
//        String clientJson = "{\"name\":\"Иван Иванов\",\"phone\":\"+79876543210\",\"username\":\"ivan\",\"password\":\"newPassword\",\"subscription\":{\"id\":1}}";
//
//        mockMvc.perform(put("/api/clients/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(clientJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Иван Иванов"));
//
//        verify(clientService, times(1)).updateClient(eq(1L), any(Client.class));
//    }
//
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deleteClient_WhenClientExists_ShouldReturnOk() throws Exception {
//        when(clientRepository.existsById(1L)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/clients/1"))
//                .andExpect(status().isOk());
//
//        verify(clientRepository, times(1)).existsById(1L);
//        verify(clientRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void deleteClient_WhenClientDoesNotExist_ShouldReturnNotFound() throws Exception {
//        when(clientRepository.existsById(1L)).thenReturn(false);
//
//        mockMvc.perform(delete("/api/clients/1"))
//                .andExpect(status().isNotFound());
//
//        verify(clientRepository, times(1)).existsById(1L);
//        verify(clientRepository, never()).deleteById(1L);
//    }
//}