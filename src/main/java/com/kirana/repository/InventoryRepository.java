package com.kirana.repository;

import com.kirana.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByVariantId(Long variantId);

    @Query("SELECT i FROM Inventory i WHERE i.stockQuantity <= i.lowStockThreshold ORDER BY i.stockQuantity ASC")
    List<Inventory> findLowStockItems();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.stockQuantity <= i.lowStockThreshold")
    long countLowStockItems();
}
