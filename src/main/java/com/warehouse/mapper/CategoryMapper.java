package com.warehouse.mapper;

import com.warehouse.dto.request.CategoryRequest;
import com.warehouse.dto.response.CategoryResponse;
import com.warehouse.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryEntity toEntity(CategoryRequest request);

    CategoryResponse toResponse(CategoryEntity entity);

    void updateEntity(CategoryRequest request,
                      @MappingTarget CategoryEntity entity);
}