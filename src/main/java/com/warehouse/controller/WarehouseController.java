package com.warehouse.controller;

import com.warehouse.dto.request.WarehouseRequest;
import com.warehouse.dto.response.WarehouseResponse;
import com.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @PostMapping
    public WarehouseResponse create(@Valid @RequestBody WarehouseRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<WarehouseResponse> getAll() {
        return service.getAll();
    }
}
