package com.sebastin.inventory_service.service;

import com.sebastin.inventory_service.dto.InventoryRequest;
import com.sebastin.inventory_service.dto.InventoryResponse;
import com.sebastin.inventory_service.entity.Inventory;
import com.sebastin.inventory_service.exception.InventoryNotFoundException;
import com.sebastin.inventory_service.mapper.InventoryMapper;
import com.sebastin.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(
                inventoryRepository,
                inventoryMapper
        );
    }

    @Test
    void createInventory_shouldReturnInventoryResponse() {

        // Arrange
        InventoryRequest request = new InventoryRequest(
                1L,
                10
        );

        Inventory inventory = new Inventory(
                1L,
                10
        );

        Inventory savedInventory = new Inventory(
                1L,
                10
        );

        InventoryResponse expectedResponse = new InventoryResponse(
                1L,
                1L,
                10
        );

        when(inventoryMapper.toEntity(request))
                .thenReturn(inventory);

        when(inventoryRepository.save(inventory))
                .thenReturn(savedInventory);

        when(inventoryMapper.toResponse(savedInventory))
                .thenReturn(expectedResponse);

        // Act
        InventoryResponse actualResponse =
                inventoryService.createInventory(request);

        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals(1L, actualResponse.getProductId());
        assertEquals(10, actualResponse.getQuantity());

        verify(inventoryMapper).toEntity(request);
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).toResponse(savedInventory);
    }

    @Test
    void getInventoryByProductId_shouldReturnResponse_whenInventoryExists() {

        // Arrange
        Long productId = 1L;

        Inventory inventory = new Inventory(
                productId,
                10
        );

        InventoryResponse expectedResponse = new InventoryResponse(
                1L,
                productId,
                10
        );

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);

        // Act
        InventoryResponse actualResponse =
                inventoryService.getInventoryByProductId(productId);

        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals(productId, actualResponse.getProductId());
        assertEquals(10, actualResponse.getQuantity());

        verify(inventoryRepository).findByProductId(productId);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void getInventoryByProductId_shouldThrowException_whenInventoryDoesNotExist() {

        // Arrange
        Long productId = 999L;

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        // Act + Assert
        InventoryNotFoundException exception =
                assertThrows(
                        InventoryNotFoundException.class,
                        () -> inventoryService.getInventoryByProductId(productId)
                );

        assertEquals(
                "Inventory not found for product ID: 999",
                exception.getMessage()
        );

        verify(inventoryRepository).findByProductId(productId);
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    void updateInventory_shouldReturnUpdatedResponse_whenInventoryExists() {

        // Arrange
        Long productId = 1L;

        InventoryRequest request =
                new InventoryRequest(productId, 25);

        Inventory inventory =
                new Inventory(productId, 10);

        InventoryResponse expectedResponse =
                new InventoryResponse(
                        1L,
                        productId,
                        25
                );

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(expectedResponse);


        // Act
        InventoryResponse actualResponse =
                inventoryService.updateInventory(productId, request);


        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals(productId, actualResponse.getProductId());
        assertEquals(25, actualResponse.getQuantity());

        verify(inventoryRepository).findByProductId(productId);
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).toResponse(inventory);
    }

    @Test
    void updateInventory_shouldThrowException_whenInventoryDoesNotExist() {

        // Arrange
        Long productId = 999L;

        InventoryRequest request =
                new InventoryRequest(productId, 25);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        // Act + Assert
        InventoryNotFoundException exception =
                assertThrows(
                        InventoryNotFoundException.class,
                        () -> inventoryService.updateInventory(
                                productId,
                                request
                        )
                );

        assertEquals(
                "Inventory not found for product ID: 999",
                exception.getMessage()
        );

        verify(inventoryRepository).findByProductId(productId);

        verify(inventoryRepository, never()).save(any());

        verifyNoInteractions(inventoryMapper);
    }

    @Test
    void deleteInventory_shouldDeleteInventory_whenInventoryExists() {

        // Arrange
        Long productId = 1L;

        Inventory inventory = new Inventory(
                productId,
                10
        );

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));


        // Act
        inventoryService.deleteInventory(productId);


        // Assert
        verify(inventoryRepository).findByProductId(productId);
        verify(inventoryRepository).delete(inventory);
    }

    @Test
    void deleteInventory_shouldThrowException_whenInventoryDoesNotExist() {

        // Arrange
        Long productId = 999L;

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());


        // Act + Assert
        InventoryNotFoundException exception =
                assertThrows(
                        InventoryNotFoundException.class,
                        () -> inventoryService.deleteInventory(productId)
                );

        assertEquals(
                "Inventory not found for product ID: 999",
                exception.getMessage()
        );

        verify(inventoryRepository).findByProductId(productId);

        verify(inventoryRepository, never())
                .delete(any());
    }
}