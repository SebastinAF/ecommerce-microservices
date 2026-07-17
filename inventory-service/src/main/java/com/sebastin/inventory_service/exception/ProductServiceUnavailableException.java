package com.sebastin.inventory_service.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException() {
        super("Product Service is currently unavailable.");
    }
}