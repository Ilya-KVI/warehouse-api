package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.ProductEntity;
import com.warehouse.exception.DuplicateSkuException;
import com.warehouse.exception.ProductNotFoundException;
import com.warehouse.mapper.ProductMapper;
import com.warehouse.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;


    private ProductRequest request;
    private ProductEntity productEntity;
    private ProductResponse response;


    @BeforeEach
    void setUp() {

        request = new ProductRequest();
        request.setName("Ноутбук Lenovo");
        request.setDescription("Игровой ноутбук");
        request.setPrice(new BigDecimal("79999.99"));
        request.setSku("LEN-001");


        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Ноутбук Lenovo");
        productEntity.setSku("LEN-001");
        productEntity.setPrice(new BigDecimal("79999.99"));


        response = new ProductResponse(
                1L,
                "Ноутбук Lenovo",
                "Игровой ноутбук",
                new BigDecimal("79999.99"),
                "LEN-001",
                null,
                null
        );
    }


    @Test
    void create_shouldCreateProductSuccessfully() {

        when(productRepository.findBySku("LEN-001"))
                .thenReturn(Optional.empty());

        when(productMapper.toEntity(request))
                .thenReturn(productEntity);

        when(productRepository.save(productEntity))
                .thenReturn(productEntity);

        when(productMapper.toResponse(productEntity))
                .thenReturn(response);


        ProductResponse result = productService.create(request);


        assertNotNull(result);
        assertEquals("LEN-001", result.getSku());
        assertEquals("Ноутбук Lenovo", result.getName());


        verify(productRepository).save(productEntity);
    }


    @Test
    void create_shouldThrowExceptionWhenSkuExists() {

        when(productRepository.findBySku("LEN-001"))
                .thenReturn(Optional.of(productEntity));


        assertThrows(
                DuplicateSkuException.class,
                () -> productService.create(request)
        );


        verify(productRepository, never())
                .save(any());
    }


    @Test
    void getById_shouldReturnProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(productEntity));

        when(productMapper.toResponse(productEntity))
                .thenReturn(response);


        ProductResponse result = productService.getById(1L);


        assertEquals(1L, result.getId());
        assertEquals("LEN-001", result.getSku());
    }


    @Test
    void getById_shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getById(1L)
        );
    }


    @Test
    void delete_shouldDeleteProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(productEntity));


        productService.delete(1L);


        verify(productRepository)
                .delete(productEntity);
    }
}