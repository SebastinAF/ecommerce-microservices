package com.sebastin.ecommerce.mapper;


import com.sebastin.ecommerce.dto.ProductRequest;
import com.sebastin.ecommerce.dto.ProductResponse;
import com.sebastin.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice());
    }

    public void updateEntity(
            ProductRequest request,
            Product product) {

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
    }
}
