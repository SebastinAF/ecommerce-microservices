package com.sebastin.inventory_service.controller;

import com.sebastin.inventory_service.dto.InventoryRequest;
import com.sebastin.inventory_service.dto.InventoryResponse;
import com.sebastin.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryRequest request
    ) {
        InventoryResponse response =
                inventoryService.createInventory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable Long productId
    ) {

        InventoryResponse response =
                inventoryService.getInventoryByProductId(productId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request
    ) {

        InventoryResponse response =
                inventoryService.updateInventory(productId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteInventory(
            @PathVariable Long productId
    ) {

        inventoryService.deleteInventory(productId);

        return ResponseEntity.noContent().build();
    }
}