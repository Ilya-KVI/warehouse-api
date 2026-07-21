package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.ProductEntity;
import com.warehouse.exception.DuplicateSkuException;
import com.warehouse.exception.ProductNotFoundException;
import com.warehouse.mapper.ProductMapper;
import com.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {

        productRepository.findBySku(request.getSku())
                .ifPresent(product -> {
                    throw new DuplicateSkuException(request.getSku());
                });

        ProductEntity product = productMapper.toEntity(request);

        ProductEntity saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {

        ProductEntity product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.findBySku(request.getSku())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateSkuException(request.getSku());
                    }
                });

        productMapper.updateEntity(request, product);

        ProductEntity saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(productMapper::toResponse);
    }

    public ProductResponse getById(Long id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
}