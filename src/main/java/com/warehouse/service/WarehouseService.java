package com.warehouse.service;

import com.warehouse.dto.request.WarehouseRequest;
import com.warehouse.dto.response.WarehouseResponse;
import com.warehouse.entity.WarehouseEntity;
import com.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository repository;

    public WarehouseService(WarehouseRepository repository) {
        this.repository = repository;
    }

    public WarehouseResponse create(WarehouseRequest request) {

        WarehouseEntity entity = new WarehouseEntity();
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());

        WarehouseEntity saved = repository.save(entity);

        return map(saved);
    }

    public List<WarehouseResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private WarehouseResponse map(WarehouseEntity entity) {
        return new WarehouseResponse(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCreatedAt()
        );
    }
}
