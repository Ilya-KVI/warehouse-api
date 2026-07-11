package com.warehouse.mapper;

import com.warehouse.dto.request.CategoryRequest;
import com.warehouse.dto.response.CategoryResponse;
import com.warehouse.entity.CategoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity toEntity(CategoryRequest request) {

        CategoryEntity entity = new CategoryEntity();

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        return entity;
    }

    public CategoryResponse toResponse(CategoryEntity entity) {

        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
