package com.warehouse.mapper;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductRequest request);

    ProductResponse toResponse(ProductEntity entity);

    void updateEntity(ProductRequest request,
                      @MappingTarget ProductEntity entity);
}