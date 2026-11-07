package com.kirana.repository;

import com.kirana.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsFeaturedTrueAndIsActiveTrue();

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByBrandIgnoreCaseAndIsActiveTrue(String brand, Pageable pageable);

    Page<Product> findByCategoryIdAndBrandIgnoreCaseAndIsActiveTrue(Long categoryId, String brand, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.nameEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.nameTe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.category.nameEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.category.nameTe) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true ORDER BY p.brand")
    List<String> findDistinctBrands();
}
