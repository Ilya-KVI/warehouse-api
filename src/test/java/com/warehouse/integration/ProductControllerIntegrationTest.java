package com.warehouse.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


class ProductControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private ProductRepository productRepository;


    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }


    @Test
    void createProduct_asAdmin_shouldCreateProduct() throws Exception {


        createAdmin();

        String token = login("admin@test.com");



        String productRequest = """
                {
                    "name": "Lenovo Legion",
                    "description": "Игровой ноутбук",
                    "price": 79999.99,
                    "sku": "LEN-LEGION-001"
                }
                """;



        MvcResult result = mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isCreated())
                .andReturn();



        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );


        assertEquals(
                "Lenovo Legion",
                json.get("name").asText()
        );


        assertEquals(
                "LEN-LEGION-001",
                json.get("sku").asText()
        );
    }


    @Test
    void createProduct_asUser_shouldReturn403() throws Exception {


        createUser();


        String token = login("user@test.com");



        String productRequest = """
                {
                    "name": "iPhone",
                    "description": "Phone",
                    "price": 99999,
                    "sku": "PHONE-001"
                }
                """;



        mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isForbidden());
    }


    @Test
    void createProduct_withoutToken_shouldReturn401() throws Exception {


        String productRequest = """
                {
                    "name": "iPhone",
                    "description": "Phone",
                    "price": 99999,
                    "sku": "PHONE-002"
                }
                """;



        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isUnauthorized());

    }


    @Test
    void getAllProducts_shouldReturnProducts() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String productRequest = """
            {
                "name": "Lenovo Legion",
                "description": "Игровой ноутбук",
                "price": 79999.99,
                "sku": "LEN-LEGION-001"
            }
            """;


        mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isCreated());


        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/api/products")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .jsonPath("$.content[0].name")
                                .value("Lenovo Legion")
                )
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .jsonPath("$.content[0].sku")
                                .value("LEN-LEGION-001")
                );
    }


    @Test
    void getProductById_shouldReturnProduct() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String productRequest = """
            {
                "name": "Lenovo Legion",
                "description": "Игровой ноутбук",
                "price": 79999.99,
                "sku": "LEN-LEGION-001"
            }
            """;


        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode createdProduct = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );


        long productId = createdProduct.get("id").asLong();


        mockMvc.perform(get("/api/products/" + productId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Lenovo Legion"))
                .andExpect(jsonPath("$.sku")
                        .value("LEN-LEGION-001"));
    }


    @Test
    void deleteProduct_shouldRemoveProduct() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String productRequest = """
            {
                "name": "Lenovo Legion",
                "description": "Игровой ноутбук",
                "price": 79999.99,
                "sku": "LEN-LEGION-001"
            }
            """;


        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode createdProduct = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );


        long productId = createdProduct.get("id").asLong();


        mockMvc.perform(delete("/api/products/" + productId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/api/products/" + productId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateProduct_shouldUpdateProduct() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String createRequest = """
            {
                "name": "Lenovo Legion",
                "description": "Игровой ноутбук",
                "price": 79999.99,
                "sku": "LEN-LEGION-001"
            }
            """;


        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();


        JsonNode createdProduct = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );


        long productId = createdProduct.get("id").asLong();



        String updateRequest = """
            {
                "name": "Lenovo Legion Updated",
                "description": "Обновленный ноутбук",
                "price": 89999.99,
                "sku": "LEN-LEGION-001"
            }
            """;


        mockMvc.perform(put("/api/products/" + productId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk());



        mockMvc.perform(get("/api/products/" + productId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Lenovo Legion Updated"))
                .andExpect(jsonPath("$.price")
                        .value(89999.99));
    }
}