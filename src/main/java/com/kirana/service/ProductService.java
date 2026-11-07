package com.kirana.service;

import com.kirana.dto.*;
import com.kirana.entity.*;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.CategoryRepository;
import com.kirana.repository.InventoryRepository;
import com.kirana.repository.ProductRepository;
import com.kirana.repository.ProductVariantRepository;
import com.kirana.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final EntityDtoMapper mapper;

    public ProductService(ProductRepository productRepository,
                          ProductVariantRepository variantRepository,
                          CategoryRepository categoryRepository,
                          InventoryRepository inventoryRepository,
                          StockMovementRepository stockMovementRepository,
                          EntityDtoMapper mapper) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.mapper = mapper;
    }

    public PagedResponse<ProductDto> getProducts(Long categoryId, String brand, String keyword,
                                                 int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;
        if (keyword != null && !keyword.isBlank()) {
            productPage = productRepository.searchProducts(keyword.trim(), pageable);
        } else if (categoryId != null && brand != null && !brand.isBlank()) {
            productPage = productRepository.findByCategoryIdAndBrandIgnoreCaseAndIsActiveTrue(categoryId, brand.trim(), pageable);
        } else if (categoryId != null) {
            productPage = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        } else if (brand != null && !brand.isBlank()) {
            productPage = productRepository.findByBrandIgnoreCaseAndIsActiveTrue(brand.trim(), pageable);
        } else {
            productPage = productRepository.findByIsActiveTrue(pageable);
        }

        List<ProductDto> content = productPage.getContent().stream()
                .map(mapper::toProductDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(mapper::toProductDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapper.toProductDto(product);
    }

    public List<String> getDistinctBrands() {
        return productRepository.findDistinctBrands();
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Product product = new Product();
        product.setCategory(category);
        product.setNameEn(request.getNameEn().trim());
        product.setNameTe(request.getNameTe().trim());
        product.setBrand(request.getBrand().trim());
        product.setDescriptionEn(request.getDescriptionEn());
        product.setDescriptionTe(request.getDescriptionTe());
        product.setImageUrl(request.getImageUrl());
        product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Product savedProduct = productRepository.save(product);

        // Process variants if provided, or add a default 1 Unit variant
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (CreateVariantRequest vReq : request.getVariants()) {
                addVariantToProduct(savedProduct, vReq);
            }
        } else {
            // Default variant
            CreateVariantRequest defaultVar = new CreateVariantRequest();
            defaultVar.setUnitName("Standard Pack");
            defaultVar.setMrp(BigDecimal.valueOf(100.00));
            defaultVar.setSellingPrice(BigDecimal.valueOf(90.00));
            defaultVar.setIsDefault(true);
            defaultVar.setInitialStock(20);
            addVariantToProduct(savedProduct, defaultVar);
        }

        return mapper.toProductDto(productRepository.findById(savedProduct.getId()).orElse(savedProduct));
    }

    private ProductVariant addVariantToProduct(Product product, CreateVariantRequest vReq) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setUnitName(vReq.getUnitName());
        variant.setSku(vReq.getSku() != null ? vReq.getSku() : "AKS-" + product.getId() + "-" + System.nanoTime() % 10000);
        variant.setMrp(vReq.getMrp());
        variant.setSellingPrice(vReq.getSellingPrice());

        if (vReq.getDiscountPercent() != null && vReq.getDiscountPercent() > 0) {
            variant.setDiscountPercent(vReq.getDiscountPercent());
        } else if (vReq.getMrp().compareTo(vReq.getSellingPrice()) > 0) {
            BigDecimal diff = vReq.getMrp().subtract(vReq.getSellingPrice());
            int pct = diff.multiply(BigDecimal.valueOf(100)).divide(vReq.getMrp(), java.math.RoundingMode.HALF_UP).intValue();
            variant.setDiscountPercent(pct);
        }

        variant.setIsDefault(vReq.getIsDefault() != null ? vReq.getIsDefault() : false);
        variant.setIsActive(true);

        ProductVariant savedVariant = variantRepository.save(variant);

        // Create inventory record
        Inventory inventory = new Inventory();
        inventory.setVariant(savedVariant);
        inventory.setStockQuantity(vReq.getInitialStock() != null ? vReq.getInitialStock() : 10);
        inventory.setLowStockThreshold(vReq.getLowStockThreshold() != null ? vReq.getLowStockThreshold() : 5);
        inventory.setLastRestockedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Record initial stock movement
        StockMovement movement = new StockMovement(
                savedVariant,
                StockMovementType.INITIAL_STOCK,
                inventory.getStockQuantity(),
                inventory.getStockQuantity(),
                "INITIAL_SETUP",
                "Product creation initial stock"
        );
        stockMovementRepository.save(movement);

        return savedVariant;
    }

    @Transactional
    public ProductDto updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (request.getCategoryId() != null && !product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        product.setNameEn(request.getNameEn());
        product.setNameTe(request.getNameTe());
        product.setBrand(request.getBrand());
        product.setDescriptionEn(request.getDescriptionEn());
        product.setDescriptionTe(request.getDescriptionTe());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());

        return mapper.toProductDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        // Soft delete / deactivate
        product.setIsActive(false);
        productRepository.save(product);
    }
}
