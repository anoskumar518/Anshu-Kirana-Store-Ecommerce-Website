package com.kirana.controller;

import com.kirana.dto.ApiResponse;
import com.kirana.dto.InventoryUpdateDto;
import com.kirana.dto.LowStockDto;
import com.kirana.entity.Inventory;
import com.kirana.entity.StockMovement;
import com.kirana.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockDto>>> getLowStockAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockAlerts()));
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ApiResponse<Inventory>> updateInventory(
            @PathVariable Long variantId,
            @Valid @RequestBody InventoryUpdateDto dto) {
        dto.setVariantId(variantId);
        Inventory updated = inventoryService.updateStock(dto);
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", updated));
    }

    @GetMapping("/{variantId}/movements")
    public ResponseEntity<ApiResponse<List<StockMovement>>> getMovements(@PathVariable Long variantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getStockMovementsByVariant(variantId)));
    }
}
