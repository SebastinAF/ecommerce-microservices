package com.sebastin.ecommerce.service;


import com.sebastin.ecommerce.dto.ProductRequest;
import com.sebastin.ecommerce.dto.ProductResponse;
import com.sebastin.ecommerce.entity.Product;
import com.sebastin.ecommerce.exception.ProductNotFoundException;
import com.sebastin.ecommerce.mapper.ProductMapper;
import com.sebastin.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productMapper);
    }

    //Test One
    @Test
    void createProduct_shouldReturnProductResponse() {

        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("gaming laptop");
        request.setPrice(new BigDecimal("50000.00"));

        Product product = new Product("Laptop", "Gaming Laptop", new BigDecimal("50000.00"));
        Product savedProduct = new Product("Laptop", "Gaming Laptop", new BigDecimal("50000.00"));
        ProductResponse expectedResponse = new ProductResponse(1L, "Laptop", "Gaming Laptop", new BigDecimal("50000.00"));

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.createProduct(request);

        assertEquals(expectedResponse, actualResponse);
        
        verify(productMapper).toEntity(request);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(savedProduct);

        assertEquals(1L, actualResponse.getId());
        assertEquals("Laptop", actualResponse.getName());
        assertEquals(new BigDecimal("50000.00"), actualResponse.getPrice());
    }

    //Test 2
    @Test
    void getProductById_shouldReturnProductResponse_whenProductExists() {

        // Arrange
        Long productId = 1L;

        Product product = new Product(
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);


        // Act
        ProductResponse actualResponse =
                productService.getProductById(productId);


        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals("Laptop", actualResponse.getName());
        assertEquals(
                new BigDecimal("50000.00"),
                actualResponse.getPrice()
        );

        verify(productRepository).findById(productId);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductById_shouldThrowProductNotFoundException_whenProductDoesNotExist() {

        // Arrange
        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Act
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(productId)
        );


        // Assert
        assertEquals(
                "Product not found with ID : 999",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);

        verifyNoInteractions(productMapper);
    }

    @Test
    void getAllProducts_shouldReturnListOfProductResponses() {

        // Arrange
        Product product1 = new Product(
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        Product product2 = new Product(
                "Mobile",
                "Samsung Mobile",
                new BigDecimal("20000.00")
        );

        List<Product> products = List.of(product1, product2);


        ProductResponse response1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        ProductResponse response2 = new ProductResponse(
                2L,
                "Mobile",
                "Samsung Mobile",
                new BigDecimal("20000.00")
        );


        when(productRepository.findAll())
                .thenReturn(products);

        when(productMapper.toResponse(product1))
                .thenReturn(response1);

        when(productMapper.toResponse(product2))
                .thenReturn(response2);

        // Act
        List<ProductResponse> actualResponses =
                productService.getAllProducts();


        // Assert
        assertEquals(2, actualResponses.size());

        assertEquals("Laptop", actualResponses.get(0).getName());
        assertEquals("Mobile", actualResponses.get(1).getName());

        assertEquals(
                new BigDecimal("50000.00"),
                actualResponses.get(0).getPrice()
        );

        assertEquals(
                new BigDecimal("20000.00"),
                actualResponses.get(1).getPrice()
        );

        verify(productRepository).findAll();

        verify(productMapper).toResponse(product1);
        verify(productMapper).toResponse(product2);
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> actualResponses =
                productService.getAllProducts();

        assertTrue(actualResponses.isEmpty());

        verify(productRepository).findAll();
        verifyNoInteractions(productMapper);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProductResponse_whenProductExists() {

        // Arrange
        Long productId = 1L;

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(new BigDecimal("60000.00"));

        Product existingProduct = new Product(
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Updated Laptop",
                "Updated Gaming Laptop",
                new BigDecimal("60000.00")
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productMapper.toResponse(existingProduct))
                .thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse =
                productService.updateProduct(productId, request);


        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals("Updated Laptop", actualResponse.getName());
        assertEquals(
                new BigDecimal("60000.00"),
                actualResponse.getPrice()
        );

        verify(productRepository).findById(productId);

        verify(productMapper)
                .updateEntity(request, existingProduct);

        verify(productMapper)
                .toResponse(existingProduct);

        verify(productRepository, never()).save(existingProduct);
    }

    @Test
    void updateProduct_shouldThrowProductNotFoundException_whenProductDoesNotExist() {

        // Arrange
        Long productId = 999L;

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(new BigDecimal("60000.00"));

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Act
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(productId, request)
        );

        // Assert
        assertEquals(
                "Product not found with ID : 999",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);

        verifyNoInteractions(productMapper);
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenProductExists() {

        // Arrange
        Long productId = 1L;

        Product product = new Product(
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository).findById(productId);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowProductNotFoundException_whenProductDoesNotExist() {

        // Arrange
        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Act
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(productId)
        );

        // Assert
        assertEquals(
                "Product not found with ID : 999",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }
}
