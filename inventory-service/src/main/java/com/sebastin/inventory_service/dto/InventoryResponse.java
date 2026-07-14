package com.sebastin.inventory_service.dto;

public class InventoryResponse {

    private Long id;
    private Long productId;
    private Integer quantity;

    public InventoryResponse() {
    }

    public InventoryResponse(Long id, Long productId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}