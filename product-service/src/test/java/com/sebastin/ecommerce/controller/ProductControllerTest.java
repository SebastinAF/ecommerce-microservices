package com.sebastin.ecommerce.controller;

import com.sebastin.ecommerce.dto.ProductRequest;
import com.sebastin.ecommerce.dto.ProductResponse;
import com.sebastin.ecommerce.exception.GlobalExceptionHandler;
import com.sebastin.ecommerce.exception.ProductNotFoundException;
import com.sebastin.ecommerce.service.ProductService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        productService = mock(ProductService.class);

        ProductController productController =
                new ProductController(productService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming Laptop");
        request.setPrice(new BigDecimal("50000.00"));

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Gaming Laptop"))
                .andExpect(jsonPath("$.price").value(50000.00));

        verify(productService)
                .createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

        ProductRequest request = new ProductRequest();

        request.setName("");
        request.setDescription("Gaming Laptop");
        request.setPrice(new BigDecimal("-50000.00"));

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Product name is required"))
                .andExpect(jsonPath("$.price")
                        .value("Product Price must be greater then zero"));

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() throws Exception {

        // Arrange
        Long productId = 1L;

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                new BigDecimal("50000.00")
        );

        when(productService.getProductById(productId))
                .thenReturn(response);
        // Act + Assert
        mockMvc.perform(
                        get("/api/products/{id}", productId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Gaming Laptop"))
                .andExpect(jsonPath("$.price").value(50000.00));

        verify(productService).getProductById(productId);
    }

    @Test
    void getProductById_shouldReturnNotFound_whenProductDoesNotExist()
            throws Exception {

        // Arrange
        Long productId = 999L;

        when(productService.getProductById(productId))
                .thenThrow(new ProductNotFoundException(productId));

        // Act + Assert
        mockMvc.perform(
                        get("/api/products/{id}", productId)
                )
                .andExpect(status().isNotFound());

        verify(productService).getProductById(productId);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {

        // Arrange
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

        when(productService.getAllProducts())
                .thenReturn(List.of(response1, response2));

        // Act + Assert
        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(50000.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Mobile"))
                .andExpect(jsonPath("$[1].price").value(20000.00));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProductsExist()
            throws Exception {

        // Arrange
        when(productService.getAllProducts())
                .thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getAllProducts();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenProductExists()
            throws Exception {

        // Arrange
        Long productId = 1L;

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(new BigDecimal("60000.00"));

        ProductResponse response = new ProductResponse(
                1L,
                "Updated Laptop",
                "Updated Gaming Laptop",
                new BigDecimal("60000.00")
        );

        when(productService.updateProduct(
                eq(productId),
                any(ProductRequest.class)
        )).thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        put("/api/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.description").value("Updated Gaming Laptop"))
                .andExpect(jsonPath("$.price").value(60000.00));

        verify(productService).updateProduct(
                eq(productId),
                any(ProductRequest.class)
        );
    }

    @Test
    void updateProduct_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {

        // Arrange
        Long productId = 1L;

        ProductRequest request = new ProductRequest();
        request.setName("");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(new BigDecimal("-60000.00"));

        // Act + Assert
        mockMvc.perform(
                        put("/api/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Product name is required"))
                .andExpect(jsonPath("$.price")
                        .value("Product Price must be greater then zero"));

        verify(productService, never())
                .updateProduct(
                        eq(productId),
                        any(ProductRequest.class)
                );
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenProductDoesNotExist()
            throws Exception {

        // Arrange
        Long productId = 999L;

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(new BigDecimal("60000.00"));

        when(productService.updateProduct(
                eq(productId),
                any(ProductRequest.class)
        )).thenThrow(new ProductNotFoundException(productId));

        // Act + Assert
        mockMvc.perform(
                        put("/api/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(
                eq(productId),
                any(ProductRequest.class)
        );
    }

    @Test
    void deleteProduct_shouldReturnNoContent_whenProductExists()
            throws Exception {

        // Arrange
        Long productId = 1L;

        // Act + Assert
        mockMvc.perform(
                        delete("/api/products/{id}", productId)
                )
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }

    @Test
    void deleteProduct_shouldReturnNotFound_whenProductDoesNotExist()
            throws Exception {

        // Arrange
        Long productId = 999L;

        doThrow(new ProductNotFoundException(productId))
                .when(productService)
                .deleteProduct(productId);

        // Act + Assert
        mockMvc.perform(
                        delete("/api/products/{id}", productId)
                )
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(productId);
    }
}