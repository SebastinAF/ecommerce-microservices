package com.sebastin.inventory_service.client;

import com.sebastin.inventory_service.client.dto.ProductResponse;
import com.sebastin.inventory_service.exception.ProductNotFoundException;
import com.sebastin.inventory_service.exception.ProductServiceUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public ProductResponse getProductById(Long productId) {

        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        } catch (ResourceAccessException ex) {
            throw new ProductServiceUnavailableException();
        }
    }
}