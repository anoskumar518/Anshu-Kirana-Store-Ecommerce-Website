package com.kirana.service;

import com.kirana.dto.InventoryUpdateDto;
import com.kirana.dto.LowStockDto;
import com.kirana.entity.Inventory;
import com.kirana.entity.ProductVariant;
import com.kirana.entity.StockMovement;
import com.kirana.entity.StockMovementType;
import com.kirana.exception.BadRequestException;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.repository.InventoryRepository;
import com.kirana.repository.ProductVariantRepository;
import com.kirana.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository variantRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductVariantRepository variantRepository,
                            StockMovementRepository stockMovementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.variantRepository = variantRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional(readOnly = true)
    public List<LowStockDto> getLowStockAlerts() {
        return inventoryRepository.findLowStockItems().stream()
                .map(inv -> new LowStockDto(
                        inv.getVariant().getId(),
                        inv.getVariant().getProduct().getId(),
                        inv.getVariant().getProduct().getNameEn(),
                        inv.getVariant().getUnitName(),
                        inv.getVariant().getSku(),
                        inv.getStockQuantity(),
                        inv.getLowStockThreshold()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Inventory updateStock(InventoryUpdateDto dto) {
        if (dto.getStockQuantity() < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        ProductVariant variant = variantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with ID: " + dto.getVariantId()));

        Inventory inventory = inventoryRepository.findByVariantId(dto.getVariantId())
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setVariant(variant);
                    return inv;
                });

        int oldQuantity = inventory.getStockQuantity() != null ? inventory.getStockQuantity() : 0;
        int diff = dto.getStockQuantity() - oldQuantity;

        inventory.setStockQuantity(dto.getStockQuantity());
        if (dto.getLowStockThreshold() != null && dto.getLowStockThreshold() >= 0) {
            inventory.setLowStockThreshold(dto.getLowStockThreshold());
        }
        if (diff > 0) {
            inventory.setLastRestockedAt(LocalDateTime.now());
        }

        Inventory saved = inventoryRepository.save(inventory);

        // Record stock movement
        if (diff != 0) {
            StockMovementType type = diff > 0 ? StockMovementType.RESTOCK : StockMovementType.ADJUSTMENT;
            StockMovement movement = new StockMovement(
                    variant,
                    type,
                    diff,
                    dto.getStockQuantity(),
                    "MANUAL_ADJUSTMENT",
                    dto.getNotes() != null ? dto.getNotes() : (diff > 0 ? "Restocked by store manager" : "Stock correction")
            );
            stockMovementRepository.save(movement);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByVariant(Long variantId) {
        return stockMovementRepository.findByVariantIdOrderByCreatedAtDesc(variantId);
    }
}
