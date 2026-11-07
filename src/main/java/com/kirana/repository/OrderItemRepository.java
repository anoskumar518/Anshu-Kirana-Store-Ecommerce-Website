package com.kirana.repository;

import com.kirana.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.productName, oi.unitName, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productName, oi.unitName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();
}
