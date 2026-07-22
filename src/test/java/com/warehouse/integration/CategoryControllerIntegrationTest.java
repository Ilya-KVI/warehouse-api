package com.warehouse.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.warehouse.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class CategoryControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAll();
    }


    @Test
    void createCategory_asAdmin_shouldCreateCategory() throws Exception {


        createAdmin();

        String token = login("admin@test.com");


        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;


        MvcResult result = mockMvc.perform(post("/api/categories")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isCreated())
                .andReturn();



        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );


        assertEquals(
                "Electronics",
                json.get("name").asText()
        );
    }


    @Test
    void createCategory_asUser_shouldReturn403() throws Exception {

        createUser();

        String token = login("user@test.com");

        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isForbidden());
    }


    @Test
    void createCategory_withoutToken_shouldReturn401() throws Exception {

        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getAllCategories_shouldReturnCategories() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Electronics"))
                .andExpect(jsonPath("$[0].description")
                        .value("Electronic devices"));
    }


    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode json = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );


        long categoryId = json.get("id").asLong();


        mockMvc.perform(get("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Electronics"))
                .andExpect(jsonPath("$.description")
                        .value("Electronic devices"));
    }


    @Test
    void updateCategory_shouldUpdateCategory() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String createRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode json = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long categoryId = json.get("id").asLong();


        String updateRequest = """
            {
                "name": "Computers",
                "description": "Computer equipment"
            }
            """;


        mockMvc.perform(put("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Computers"))
                .andExpect(jsonPath("$.description")
                        .value("Computer equipment"));
    }


    @Test
    void deleteCategory_shouldRemoveCategory() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode json = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long categoryId = json.get("id").asLong();


        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


    @Test
    void createCategory_withExistingName_shouldReturn409() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String categoryRequest = """
            {
                "name": "Electronics",
                "description": "Electronic devices"
            }
            """;


        mockMvc.perform(post("/api/categories")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isCreated());


        mockMvc.perform(post("/api/categories")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isConflict());
    }




}