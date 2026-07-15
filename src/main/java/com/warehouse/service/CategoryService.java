package com.warehouse.service;

import com.warehouse.dto.request.CategoryRequest;
import com.warehouse.dto.response.CategoryResponse;
import com.warehouse.entity.CategoryEntity;
import com.warehouse.exception.CategoryAlreadyExistsException;
import com.warehouse.exception.CategoryNotFoundException;
import com.warehouse.mapper.CategoryMapper;
import com.warehouse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        CategoryEntity entity = categoryMapper.toEntity(request);

        CategoryEntity saved = categoryRepository.save(entity);

        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {

        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new CategoryAlreadyExistsException(request.getName());
                    }
                });

        categoryMapper.updateEntity(request, entity);

        CategoryEntity saved = categoryRepository.save(entity);

        return categoryMapper.toResponse(saved);
    }

    public List<CategoryResponse> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {

        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toResponse(entity);
    }

    public void delete(Long id) {

        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(entity);
    }
}