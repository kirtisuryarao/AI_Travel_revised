package com.lekha.travel_planner.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lekha.travel_planner.dto.ProductRequest;
import com.lekha.travel_planner.dto.ProductResponse;
import com.lekha.travel_planner.entity.Product;
import com.lekha.travel_planner.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> listActiveProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ProductResponse> listAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    public Product findProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category().trim());
        product.setStockQuantity(request.stockQuantity());
        product.setActive(request.active());
        product.setImageUrl(request.imageUrl());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getStockQuantity(),
            product.isActive(),
            product.getImageUrl(),
            product.getCreatedAt()
        );
    }
}
