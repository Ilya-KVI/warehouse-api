package com.warehouse.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.entity.Role;
import com.warehouse.entity.UserEntity;
import com.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected ObjectMapper objectMapper;


    protected void createAdmin() {

        if (userRepository.existsByEmail("admin@test.com")) {
            return;
        }


        UserEntity admin = UserEntity.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_ADMIN)
                .build();


        userRepository.save(admin);
    }


    protected void createUser() {


        if (userRepository.existsByEmail("user@test.com")) {
            return;
        }


        UserEntity user = UserEntity.builder()
                .firstName("User")
                .lastName("User")
                .email("user@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_USER)
                .build();


        userRepository.save(user);
    }


    protected String login(String email) throws Exception {


        String request = """
            {
                "email": "%s",
                "password": "password123"
            }
            """.formatted(email);



        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn();



        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );


        return json.get("token").asText();
    }

}