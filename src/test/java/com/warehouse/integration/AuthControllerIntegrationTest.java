package com.warehouse.integration;

import com.warehouse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }


    @Test
    void register_shouldCreateUserSuccessfully() throws Exception {

        String request = """
                {
                    "firstName": "Ivan",
                    "lastName": "Petrov",
                    "email": "ivan@test.com",
                    "password": "password123"
                }
                """;


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());


        assertTrue(userRepository.existsByEmail("ivan@test.com"));
    }


    @Test
    void login_shouldReturnJwtToken() throws Exception {

        String registerRequest = """
            {
                "firstName": "Alex",
                "lastName": "Ivanov",
                "email": "alex@test.com",
                "password": "password123"
            }
            """;


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());


        String loginRequest = """
            {
                "email": "alex@test.com",
                "password": "password123"
            }
            """;


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk());
    }


    @Test
    void register_withExistingEmail_shouldReturn409() throws Exception {

        String request = """
            {
                "firstName": "Ivan",
                "lastName": "Petrov",
                "email": "ivan@test.com",
                "password": "password123"
            }
            """;


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict());
    }


    @Test
    void login_withWrongPassword_shouldReturn401() throws Exception {

        String registerRequest = """
            {
                "firstName": "Alex",
                "lastName": "Ivanov",
                "email": "alex@test.com",
                "password": "password123"
            }
            """;


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());


        System.out.println("Пользователь существует: " + userRepository.existsByEmail("alex@test.com"));


        String loginRequest = """
            {
                "email": "alex@test.com",
                "password": "wrongPassword"
            }
            """;


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }
}