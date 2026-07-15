package com.warehouse.service;

import com.warehouse.dto.request.WarehouseRequest;
import com.warehouse.dto.response.WarehouseResponse;
import com.warehouse.entity.WarehouseEntity;
import com.warehouse.exception.WarehouseNotFoundException;
import com.warehouse.mapper.WarehouseMapper;
import com.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseResponse create(WarehouseRequest request) {

        WarehouseEntity entity = warehouseMapper.toEntity(request);

        WarehouseEntity saved = repository.save(entity);

        return warehouseMapper.toResponse(saved);
    }

    public WarehouseResponse update(Long id, WarehouseRequest request) {

        WarehouseEntity entity = repository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        warehouseMapper.updateEntity(request, entity);

        WarehouseEntity saved = repository.save(entity);

        return warehouseMapper.toResponse(saved);
    }

    public WarehouseResponse getById(Long id) {

        WarehouseEntity entity = repository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        return warehouseMapper.toResponse(entity);
    }

    public List<WarehouseResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    public void delete(Long id) {

        WarehouseEntity entity = repository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        repository.delete(entity);
    }
}