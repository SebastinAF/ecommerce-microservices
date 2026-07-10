package com.sebastin.ecommerce.service;

import com.sebastin.ecommerce.dto.ProductRequest;
import com.sebastin.ecommerce.dto.ProductResponse;
import com.sebastin.ecommerce.entity.Product;
import com.sebastin.ecommerce.exception.ProductNotFoundException;
import com.sebastin.ecommerce.mapper.ProductMapper;
import com.sebastin.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    //This is the place of dependency injection happening (Constructor Injection)
    public ProductService (ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product saveProduct = productRepository.save(product);
        return productMapper.toResponse(saveProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productMapper.updateEntity(request, existingProduct);
        return productMapper.toResponse(existingProduct);
    }

    public void deleteProduct(Long id) {

        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(existingProduct);
    }
}
