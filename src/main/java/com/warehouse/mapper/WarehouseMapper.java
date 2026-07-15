package com.warehouse.mapper;

import com.warehouse.dto.request.WarehouseRequest;
import com.warehouse.dto.response.WarehouseResponse;
import com.warehouse.entity.WarehouseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseEntity toEntity(WarehouseRequest request);

    WarehouseResponse toResponse(WarehouseEntity entity);

    void updateEntity(WarehouseRequest request, @MappingTarget WarehouseEntity entity);
}
