package com.warehouse.service;

import com.warehouse.dto.request.WarehouseRequest;
import com.warehouse.dto.response.WarehouseResponse;
import com.warehouse.entity.WarehouseEntity;
import com.warehouse.exception.WarehouseNotFoundException;
import com.warehouse.mapper.WarehouseMapper;
import com.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {


    @Mock
    private WarehouseRepository repository;

    @Mock
    private WarehouseMapper warehouseMapper;


    @InjectMocks
    private WarehouseService warehouseService;


    private WarehouseRequest request;
    private WarehouseEntity warehouseEntity;
    private WarehouseResponse response;


    @BeforeEach
    void setUp() {

        request = new WarehouseRequest(
                "Главный склад",
                "Москва, ул. Ленина 1"
        );


        warehouseEntity = new WarehouseEntity();
        warehouseEntity.setId(1L);
        warehouseEntity.setName("Главный склад");
        warehouseEntity.setAddress("Москва, ул. Ленина 1");


        response = new WarehouseResponse(
                1L,
                "Главный склад",
                "Москва, ул. Ленина 1",
                LocalDateTime.now()
        );
    }


    @Test
    void create_shouldCreateWarehouseSuccessfully() {

        when(warehouseMapper.toEntity(request))
                .thenReturn(warehouseEntity);

        when(repository.save(warehouseEntity))
                .thenReturn(warehouseEntity);

        when(warehouseMapper.toResponse(warehouseEntity))
                .thenReturn(response);


        WarehouseResponse result =
                warehouseService.create(request);


        assertNotNull(result);
        assertEquals("Главный склад", result.getName());


        verify(repository)
                .save(warehouseEntity);
    }


    @Test
    void getById_shouldReturnWarehouse() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(warehouseEntity));

        when(warehouseMapper.toResponse(warehouseEntity))
                .thenReturn(response);


        WarehouseResponse result =
                warehouseService.getById(1L);


        assertEquals(1L, result.getId());
        assertEquals("Главный склад", result.getName());
    }


    @Test
    void getById_shouldThrowExceptionWhenNotFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                WarehouseNotFoundException.class,
                () -> warehouseService.getById(1L)
        );
    }


    @Test
    void getAll_shouldReturnWarehouses() {

        when(repository.findAll())
                .thenReturn(List.of(warehouseEntity));

        when(warehouseMapper.toResponse(warehouseEntity))
                .thenReturn(response);


        List<WarehouseResponse> result =
                warehouseService.getAll();


        assertEquals(1, result.size());
        assertEquals(
                "Главный склад",
                result.get(0).getName()
        );


        verify(repository)
                .findAll();
    }


    @Test
    void delete_shouldDeleteWarehouse() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(warehouseEntity));


        warehouseService.delete(1L);


        verify(repository, times(1))
                .delete(warehouseEntity);
    }
}