package com.sebastin.inventory_service.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }
}