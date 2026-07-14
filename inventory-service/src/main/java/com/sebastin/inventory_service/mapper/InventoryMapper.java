package com.sebastin.inventory_service.mapper;

import com.sebastin.inventory_service.dto.InventoryRequest;
import com.sebastin.inventory_service.dto.InventoryResponse;
import com.sebastin.inventory_service.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toEntity(InventoryRequest request) {

        return new Inventory(
                request.getProductId(),
                request.getQuantity()
        );
    }

    public InventoryResponse toResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity()
        );
    }
}