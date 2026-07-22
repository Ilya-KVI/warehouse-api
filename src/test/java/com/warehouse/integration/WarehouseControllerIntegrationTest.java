package com.warehouse.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


class WarehouseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void cleanDatabase() {
        warehouseRepository.deleteAll();
    }


    @Test
    void createWarehouse_asAdmin_shouldCreateWarehouse() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        assertEquals(
                "Main Warehouse",
                json.get("name").asText()
        );

        assertEquals(
                "Moscow, Lenina 10",
                json.get("address").asText()
        );
    }


    @Test
    void createWarehouse_asUser_shouldReturn403() throws Exception {

        createUser();

        String token = login("user@test.com");

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isForbidden());
    }


    @Test
    void createWarehouse_withoutToken_shouldReturn401() throws Exception {

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getAllWarehouses_shouldReturnWarehouses() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/warehouses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Main Warehouse"))
                .andExpect(jsonPath("$[0].address")
                        .value("Moscow, Lenina 10"));
    }


    @Test
    void getWarehouseById_shouldReturnWarehouse() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdWarehouse = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long warehouseId = createdWarehouse.get("id").asLong();

        mockMvc.perform(get("/api/warehouses/" + warehouseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Main Warehouse"))
                .andExpect(jsonPath("$.address")
                        .value("Moscow, Lenina 10"));
    }


    @Test
    void updateWarehouse_shouldUpdateWarehouse() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String createRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdWarehouse = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long warehouseId = createdWarehouse.get("id").asLong();

        String updateRequest = """
            {
                "name": "Central Warehouse",
                "address": "Saint Petersburg, Nevsky 1"
            }
            """;

        mockMvc.perform(put("/api/warehouses/" + warehouseId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Central Warehouse"))
                .andExpect(jsonPath("$.address")
                        .value("Saint Petersburg, Nevsky 1"));
    }


    @Test
    void deleteWarehouse_shouldRemoveWarehouse() throws Exception {

        createAdmin();

        String token = login("admin@test.com");

        String warehouseRequest = """
            {
                "name": "Main Warehouse",
                "address": "Moscow, Lenina 10"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/warehouses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(warehouseRequest))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdWarehouse = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long warehouseId = createdWarehouse.get("id").asLong();

        mockMvc.perform(delete("/api/warehouses/" + warehouseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/warehouses/" + warehouseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


    @Test
    void getWarehouseById_whenWarehouseNotFound_shouldReturn404() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        mockMvc.perform(get("/api/warehouses/999999")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteWarehouse_whenWarehouseNotFound_shouldReturn404() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        mockMvc.perform(delete("/api/warehouses/999999")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateWarehouse_whenWarehouseNotFound_shouldReturn404() throws Exception {

        createAdmin();

        String token = login("admin@test.com");


        String updateRequest = """
            {
                "name": "Updated Warehouse",
                "address": "New Address"
            }
            """;


        mockMvc.perform(put("/api/warehouses/999999")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isNotFound());
    }



}

