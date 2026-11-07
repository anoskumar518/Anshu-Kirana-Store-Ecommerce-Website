package com.kirana.service;

import com.kirana.dto.AddToCartRequest;
import com.kirana.dto.CartDto;
import com.kirana.entity.*;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.CartItemRepository;
import com.kirana.repository.CartRepository;
import com.kirana.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private AuthService authService;

    @Spy
    private EntityDtoMapper mapper = new EntityDtoMapper();

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cartService, "freeDeliveryThreshold", BigDecimal.valueOf(299.00));
        ReflectionTestUtils.setField(cartService, "standardDeliveryCharge", BigDecimal.valueOf(20.00));

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Test Customer");
        testUser.setPhone("9876543211");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setNameEn("Sona Masoori Rice");
        testProduct.setNameTe("సోనా మసూరి బియ్యం");
        testProduct.setBrand("Anshu Select");

        testVariant = new ProductVariant();
        testVariant.setId(100L);
        testVariant.setProduct(testProduct);
        testVariant.setUnitName("5 kg Bag");
        testVariant.setMrp(BigDecimal.valueOf(350.00));
        testVariant.setSellingPrice(BigDecimal.valueOf(315.00));
        testVariant.setIsActive(true);

        testInventory = new Inventory();
        testInventory.setId(1000L);
        testInventory.setVariant(testVariant);
        testInventory.setStockQuantity(25);
        testInventory.setLowStockThreshold(5);
        testVariant.setInventory(testInventory);

        testCart = new Cart();
        testCart.setId(50L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    @DisplayName("getCartDto should return empty CartDto when user has no cart, without calling cartRepository.save()")
    void testGetCartDto_WhenNoCartExists_ShouldReturnEmptyCartDtoWithoutSaving() {
        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());

        CartDto dto = cartService.getCartDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals(0, dto.getTotalItemsCount());
        assertEquals(BigDecimal.ZERO, dto.getSubtotal());
        assertTrue(dto.getItems().isEmpty());

        // CRUCIAL: Must NOT call cartRepository.save during a read-only getCartDto() operation!
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("getCartDto should return populated CartDto when cart exists with items")
    void testGetCartDto_WhenCartExists_ShouldReturnPopulatedCartDto() {
        CartItem item = new CartItem(testCart, testVariant, 2);
        item.setId(500L);
        testCart.getItems().add(item);

        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));

        CartDto dto = cartService.getCartDto();

        assertNotNull(dto);
        assertEquals(50L, dto.getId());
        assertEquals(2, dto.getTotalItemsCount());
        assertEquals(BigDecimal.valueOf(630.00), dto.getSubtotal());
        assertEquals(1, dto.getItems().size());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("addToCart should create a new Cart for user in writable transaction if absent")
    void testAddToCart_WhenNoCartExists_ShouldCreateCartAndAddItem() {
        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId()))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        when(variantRepository.findById(100L)).thenReturn(Optional.of(testVariant));
        when(cartItemRepository.findByCartIdAndVariantId(50L, 100L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddToCartRequest req = new AddToCartRequest();
        req.setVariantId(100L);
        req.setQuantity(2);

        CartDto result = cartService.addToCart(req);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }
}
