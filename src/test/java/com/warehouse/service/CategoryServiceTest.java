package com.warehouse.service;

import com.warehouse.dto.request.CategoryRequest;
import com.warehouse.dto.response.CategoryResponse;
import com.warehouse.entity.CategoryEntity;
import com.warehouse.exception.CategoryAlreadyExistsException;
import com.warehouse.exception.CategoryNotFoundException;
import com.warehouse.mapper.CategoryMapper;
import com.warehouse.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;


    private CategoryRequest request;
    private CategoryEntity categoryEntity;
    private CategoryResponse response;


    @BeforeEach
    void setUp() {

        request = new CategoryRequest();
        request.setName("Ноутбуки");
        request.setDescription("Категория ноутбуков");


        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Ноутбуки");
        categoryEntity.setDescription("Категория ноутбуков");


        response = new CategoryResponse(
                1L,
                "Ноутбуки",
                "Категория ноутбуков"
        );
    }


    @Test
    void create_shouldCreateCategorySuccessfully() {

        when(categoryRepository.existsByName("Ноутбуки"))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(categoryEntity);

        when(categoryRepository.save(categoryEntity))
                .thenReturn(categoryEntity);

        when(categoryMapper.toResponse(categoryEntity))
                .thenReturn(response);


        CategoryResponse result = categoryService.create(request);


        assertNotNull(result);
        assertEquals("Ноутбуки", result.getName());

        verify(categoryRepository)
                .save(categoryEntity);
    }


    @Test
    void create_shouldThrowExceptionWhenCategoryExists() {

        when(categoryRepository.existsByName("Ноутбуки"))
                .thenReturn(true);


        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.create(request)
        );


        verify(categoryRepository, never())
                .save(any());
    }


    @Test
    void getById_shouldReturnCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(categoryEntity));

        when(categoryMapper.toResponse(categoryEntity))
                .thenReturn(response);


        CategoryResponse result = categoryService.getById(1L);


        assertEquals(1L, result.getId());
        assertEquals("Ноутбуки", result.getName());
    }


    @Test
    void getById_shouldThrowExceptionWhenNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getById(1L)
        );
    }


    @Test
    void delete_shouldDeleteCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(categoryEntity));


        categoryService.delete(1L);


        verify(categoryRepository, times(1))
                .delete(categoryEntity);
    }


    @Test
    void getAll_shouldReturnCategories() {

        when(categoryRepository.findAll())
                .thenReturn(List.of(categoryEntity));

        when(categoryMapper.toResponse(categoryEntity))
                .thenReturn(response);

        List<CategoryResponse> result = categoryService.getAll();


        assertEquals(1, result.size());
        assertEquals("Ноутбуки", result.get(0).getName());


        verify(categoryRepository)
                .findAll();
    }
}