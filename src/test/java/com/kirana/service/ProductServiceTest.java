package com.kirana.service;

import com.kirana.dto.CreateProductRequest;
import com.kirana.dto.CreateVariantRequest;
import com.kirana.dto.PagedResponse;
import com.kirana.dto.ProductDto;
import com.kirana.entity.*;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Spy
    private EntityDtoMapper mapper = new EntityDtoMapper();

    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;
    private ProductVariant testVariant;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setNameEn("Rice & Grains");
        testCategory.setNameTe("బియ్యం మరియు ధాన్యాలు");
        testCategory.setSlug("rice-grains");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setCategory(testCategory);
        testProduct.setNameEn("Kurnool Sona Masoori Rice");
        testProduct.setNameTe("కర్నూల్ సోనా మసూరి బియ్యం");
        testProduct.setBrand("Anshu Select");
        testProduct.setIsActive(true);
        testProduct.setIsFeatured(true);

        testVariant = new ProductVariant();
        testVariant.setId(100L);
        testVariant.setProduct(testProduct);
        testVariant.setUnitName("5 kg Bag");
        testVariant.setSku("RICE-SM-5KG");
        testVariant.setMrp(BigDecimal.valueOf(350.00));
        testVariant.setSellingPrice(BigDecimal.valueOf(315.00));
        testVariant.setIsActive(true);
        testVariant.setIsDefault(true);

        Inventory inv = new Inventory();
        inv.setId(500L);
        inv.setVariant(testVariant);
        inv.setStockQuantity(25);
        inv.setLowStockThreshold(5);
        testVariant.setInventory(inv);

        List<ProductVariant> variants = new ArrayList<>();
        variants.add(testVariant);
        testProduct.setVariants(variants);
    }

    @Test
    @DisplayName("getProducts should return paginated ProductDto list")
    void testGetProducts_ShouldReturnPagedProducts() {
        Page<Product> page = new PageImpl<>(List.of(testProduct));
        when(productRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        PagedResponse<ProductDto> result = productService.getProducts(null, null, null, 0, 16, "id", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        ProductDto pDto = result.getContent().get(0);
        assertEquals("Kurnool Sona Masoori Rice", pDto.getNameEn());
        assertEquals("Anshu Select", pDto.getBrand());
        assertNotNull(pDto.getDefaultVariant());
        assertEquals(BigDecimal.valueOf(315.00), pDto.getDefaultVariant().getSellingPrice());
    }

    @Test
    @DisplayName("createProduct should persist product, variant, inventory, and stock movement")
    void testCreateProduct_ShouldPersistAllEntities() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(variantRepository.save(any(ProductVariant.class))).thenReturn(testVariant);
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        CreateProductRequest req = new CreateProductRequest();
        req.setCategoryId(1L);
        req.setNameEn("Kurnool Sona Masoori Rice");
        req.setNameTe("కర్నూల్ సోనా మసూరి బియ్యం");
        req.setBrand("Anshu Select");

        CreateVariantRequest vReq = new CreateVariantRequest();
        vReq.setUnitName("5 kg Bag");
        vReq.setMrp(BigDecimal.valueOf(350.00));
        vReq.setSellingPrice(BigDecimal.valueOf(315.00));
        vReq.setInitialStock(25);
        req.setVariants(List.of(vReq));

        ProductDto created = productService.createProduct(req);

        assertNotNull(created);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(variantRepository, times(1)).save(any(ProductVariant.class));
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }
}
