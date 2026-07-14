package com.sebastin.inventory_service.controller;

import com.sebastin.inventory_service.dto.InventoryRequest;
import com.sebastin.inventory_service.exception.GlobalExceptionHandler;
import com.sebastin.inventory_service.exception.InventoryNotFoundException;
import com.sebastin.inventory_service.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import com.sebastin.inventory_service.dto.InventoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest {

    private MockMvc mockMvc;

    private InventoryService inventoryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        inventoryService = mock(InventoryService.class);

        InventoryController inventoryController =
                new InventoryController(inventoryService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(inventoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createInventory_shouldReturnCreatedInventory()
            throws Exception {

        // Arrange
        InventoryRequest request =
                new InventoryRequest(1L, 10);

        InventoryResponse response =
                new InventoryResponse(1L, 1L, 10);

        when(inventoryService.createInventory(any(InventoryRequest.class)))
                .thenReturn(response);


        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/inventories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(10));


        verify(inventoryService)
                .createInventory(any(InventoryRequest.class));
    }

    @Test
    void getInventoryByProductId_shouldReturnInventoryResponse()
            throws Exception {

        // Arrange
        Long productId = 1L;

        InventoryResponse response =
                new InventoryResponse(
                        1L,
                        productId,
                        10
                );

        when(inventoryService.getInventoryByProductId(productId))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get("/api/v1/inventories/product/{productId}", productId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(inventoryService)
                .getInventoryByProductId(productId);
    }

    @Test
    void getInventoryByProductId_shouldReturnNotFound_whenInventoryDoesNotExist()
            throws Exception {

        // Arrange
        Long productId = 999L;

        when(inventoryService.getInventoryByProductId(productId))
                .thenThrow(new InventoryNotFoundException(productId));

        // Act + Assert
        mockMvc.perform(
                        get("/api/v1/inventories/product/{productId}", productId)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Inventory not found for product ID: 999")
                );

        verify(inventoryService)
                .getInventoryByProductId(productId);
    }

    @Test
    void createInventory_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {

        // Arrange
        InventoryRequest request =
                new InventoryRequest(-1L, -10);

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/inventories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.productId")
                                .value("Product ID must be greater than zero")
                )
                .andExpect(
                        jsonPath("$.quantity")
                                .value("Quantity cannot be negative")
                );

        verifyNoInteractions(inventoryService);
    }

    @Test
    void updateInventory_shouldReturnUpdatedInventory_whenInventoryExists()
            throws Exception {

        // Arrange
        Long productId = 1L;

        InventoryRequest request =
                new InventoryRequest(productId, 25);

        InventoryResponse response =
                new InventoryResponse(
                        1L,
                        productId,
                        25
                );

        when(inventoryService.updateInventory(
                eq(productId),
                any(InventoryRequest.class)
        )).thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        put(
                                "/api/v1/inventories/product/{productId}",
                                productId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(25));


        verify(inventoryService)
                .updateInventory(
                        eq(productId),
                        any(InventoryRequest.class)
                );
    }

    @Test
    void updateInventory_shouldReturnNotFound_whenInventoryDoesNotExist()
            throws Exception {

        // Arrange
        Long productId = 999L;

        InventoryRequest request =
                new InventoryRequest(productId, 25);

        when(inventoryService.updateInventory(
                eq(productId),
                any(InventoryRequest.class)
        )).thenThrow(
                new InventoryNotFoundException(productId)
        );

        // Act + Assert
        mockMvc.perform(
                        put(
                                "/api/v1/inventories/product/{productId}",
                                productId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Inventory not found for product ID: 999"
                                )
                );

        verify(inventoryService)
                .updateInventory(
                        eq(productId),
                        any(InventoryRequest.class)
                );
    }

    @Test
    void deleteInventory_shouldReturnNoContent_whenInventoryExists()
            throws Exception {

        Long productId = 1L;

        mockMvc.perform(
                        delete(
                                "/api/v1/inventories/product/{productId}",
                                productId
                        )
                )
                .andExpect(status().isNoContent());

        verify(inventoryService).deleteInventory(productId);
    }

    @Test
    void deleteInventory_shouldReturnNotFound_whenInventoryDoesNotExist()
            throws Exception {

        Long productId = 999L;

        doThrow(new InventoryNotFoundException(productId))
                .when(inventoryService)
                .deleteInventory(productId);

        mockMvc.perform(
                        delete(
                                "/api/v1/inventories/product/{productId}",
                                productId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Inventory not found for product ID: 999")
                );

        verify(inventoryService).deleteInventory(productId);
    }
}