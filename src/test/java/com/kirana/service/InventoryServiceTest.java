package com.kirana.service;

import com.kirana.dto.InventoryUpdateDto;
import com.kirana.entity.*;
import com.kirana.repository.InventoryRepository;
import com.kirana.repository.ProductVariantRepository;
import com.kirana.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testVariant = new ProductVariant();
        testVariant.setId(100L);
        testVariant.setUnitName("1 kg");

        testInventory = new Inventory();
        testInventory.setId(50L);
        testInventory.setVariant(testVariant);
        testInventory.setStockQuantity(10);
        testInventory.setLowStockThreshold(5);
    }

    @Test
    @DisplayName("updateStock should update quantity and record RESTOCK movement when stock increases")
    void testUpdateStock_Restock() {
        when(variantRepository.findById(100L)).thenReturn(Optional.of(testVariant));
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        InventoryUpdateDto dto = new InventoryUpdateDto();
        dto.setVariantId(100L);
        dto.setStockQuantity(30);
        dto.setNotes("Supplier delivery");

        Inventory updated = inventoryService.updateStock(dto);

        assertNotNull(updated);
        assertEquals(30, updated.getStockQuantity());
        verify(inventoryRepository, times(1)).save(testInventory);
        verify(stockMovementRepository, times(1)).save(argThat(movement ->
                movement.getMovementType() == StockMovementType.RESTOCK &&
                movement.getQuantityChanged() == 20 &&
                movement.getBalanceQuantity() == 30
        ));
    }
}
