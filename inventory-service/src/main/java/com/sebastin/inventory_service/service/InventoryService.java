package com.sebastin.inventory_service.service;

import com.sebastin.inventory_service.dto.InventoryRequest;
import com.sebastin.inventory_service.dto.InventoryResponse;
import com.sebastin.inventory_service.entity.Inventory;
import com.sebastin.inventory_service.exception.InventoryNotFoundException;
import com.sebastin.inventory_service.mapper.InventoryMapper;
import com.sebastin.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(
            InventoryRepository inventoryRepository,
            InventoryMapper inventoryMapper
    ) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryResponse createInventory(InventoryRequest request) {

        Inventory inventory = inventoryMapper.toEntity(request);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(savedInventory);
    }

    public InventoryResponse getInventoryByProductId(Long productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId)
                );

        return inventoryMapper.toResponse(inventory);
    }

    public InventoryResponse updateInventory(
            Long productId,
            InventoryRequest request
    ) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId)
                );

        inventory.setQuantity(request.getQuantity());

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    public void deleteInventory(Long productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId)
                );

        inventoryRepository.delete(inventory);
    }
}