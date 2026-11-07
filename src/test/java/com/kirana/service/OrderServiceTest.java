package com.kirana.service;

import com.kirana.dto.CreateOrderRequest;
import com.kirana.dto.OrderDto;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuthService authService;

    @Spy
    private EntityDtoMapper mapper = new EntityDtoMapper();

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "freeDeliveryThreshold", BigDecimal.valueOf(299.00));
        ReflectionTestUtils.setField(orderService, "standardDeliveryCharge", BigDecimal.valueOf(20.00));

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Ramesh Kumar");
        testUser.setPhone("7993542873");

        Product product = new Product();
        product.setId(10L);
        product.setNameEn("Aashirvaad Atta");
        product.setNameTe("ఆశీర్వాద్ గోధుమ పిండి");

        testVariant = new ProductVariant();
        testVariant.setId(100L);
        testVariant.setProduct(product);
        testVariant.setUnitName("5 kg Bag");
        testVariant.setSellingPrice(BigDecimal.valueOf(275.00));

        testInventory = new Inventory();
        testInventory.setId(500L);
        testInventory.setVariant(testVariant);
        testInventory.setStockQuantity(20);

        testCart = new Cart();
        testCart.setId(200L);
        testCart.setUser(testUser);

        CartItem item = new CartItem(testCart, testVariant, 2);
        item.setId(300L);
        List<CartItem> items = new ArrayList<>();
        items.add(item);
        testCart.setItems(items);
    }

    @Test
    @DisplayName("placeOrder should deduct inventory, save payment, clear cart and return OrderDto")
    void testPlaceOrder_Success() {
        when(authService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(cartService.getOrCreateCartForCurrentUser()).thenReturn(testCart);
        when(inventoryRepository.findByVariantId(100L)).thenReturn(Optional.of(testInventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(999L);
            return o;
        });

        CreateOrderRequest req = new CreateOrderRequest();
        req.setCustomerName("Ramesh Kumar");
        req.setCustomerPhone("7993542873");
        req.setDeliveryVillage("Repudi");
        req.setStreetArea("Main Bazaar");
        req.setPaymentMethod(PaymentMethod.COD);

        OrderDto orderDto = orderService.placeOrder(req);

        assertNotNull(orderDto);
        assertEquals(999L, orderDto.getId());
        assertEquals("Ramesh Kumar", orderDto.getCustomerName());
        assertEquals(BigDecimal.valueOf(550.00), orderDto.getSubtotal()); // 2 * 275
        assertEquals(BigDecimal.ZERO, orderDto.getDeliveryCharge()); // >= 299 threshold
        assertEquals(BigDecimal.valueOf(550.00), orderDto.getTotalAmount());

        // Inventory should be reduced from 20 to 18
        assertEquals(18, testInventory.getStockQuantity());
        verify(inventoryRepository, times(1)).save(testInventory);
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(cartService, times(1)).clearCart(testCart);
    }
}
