package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.exception.DuplicateSkuException;
import com.warehouse.exception.ProductNotFoundException;
import com.warehouse.entity.ProductEntity;
import com.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {

        productRepository.findBySku(request.getSku()).ifPresent(product -> {throw new DuplicateSkuException(request.getSku());
        });

        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());

        ProductEntity saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    public ProductResponse getById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return mapToResponse(product);
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(ProductEntity product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSku(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}