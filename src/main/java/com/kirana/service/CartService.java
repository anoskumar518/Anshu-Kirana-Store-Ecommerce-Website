package com.kirana.service;

import com.kirana.dto.AddToCartRequest;
import com.kirana.dto.CartDto;
import com.kirana.dto.CartItemDto;
import com.kirana.entity.Cart;
import com.kirana.entity.CartItem;
import com.kirana.entity.Inventory;
import com.kirana.entity.ProductVariant;
import com.kirana.entity.User;
import com.kirana.exception.BadRequestException;
import com.kirana.exception.InsufficientStockException;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.CartItemRepository;
import com.kirana.repository.CartRepository;
import com.kirana.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final AuthService authService;
    private final EntityDtoMapper mapper;

    @Value("${app.store.free-delivery-threshold:299.00}")
    private BigDecimal freeDeliveryThreshold = BigDecimal.valueOf(299.00);

    @Value("${app.store.delivery-charge:20.00}")
    private BigDecimal standardDeliveryCharge = BigDecimal.valueOf(20.00);

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductVariantRepository variantRepository,
                       AuthService authService,
                       EntityDtoMapper mapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.authService = authService;
        this.mapper = mapper;
    }

    @Transactional
    public Cart getOrCreateCartForCurrentUser() {
        User user = authService.getCurrentAuthenticatedUser();
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto() {
        User user = authService.getCurrentAuthenticatedUser();
        return cartRepository.findByUserId(user.getId())
                .map(this::buildCartDto)
                .orElseGet(this::buildEmptyCartDto);
    }

    public CartDto buildEmptyCartDto() {
        CartDto dto = new CartDto();
        dto.setFreeDeliveryThreshold(freeDeliveryThreshold);
        dto.setAmountNeededForFreeDelivery(freeDeliveryThreshold);
        return dto;
    }

    @Transactional
    public CartDto addToCart(AddToCartRequest request) {
        Cart cart = getOrCreateCartForCurrentUser();

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with ID: " + request.getVariantId()));

        if (Boolean.FALSE.equals(variant.getIsActive())) {
            throw new BadRequestException("This product variant is currently not available");
        }

        Inventory inventory = variant.getInventory();
        int availableStock = inventory != null ? inventory.getStockQuantity() : 0;

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());

        int newTotalQuantity = request.getQuantity();
        CartItem cartItem;

        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            newTotalQuantity = cartItem.getQuantity() + request.getQuantity();
            if (newTotalQuantity > availableStock) {
                throw new InsufficientStockException("Cannot add " + request.getQuantity() + " more items. Only " + availableStock + " units available in store.");
            }
            cartItem.setQuantity(newTotalQuantity);
        } else {
            if (newTotalQuantity > availableStock) {
                throw new InsufficientStockException("Only " + availableStock + " units available in store.");
            }
            cartItem = new CartItem(cart, variant, newTotalQuantity);
            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        return buildCartDto(cart);
    }

    @Transactional
    public CartDto updateCartItemQuantity(Long cartItemId, int quantity) {
        Cart cart = getOrCreateCartForCurrentUser();

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            Inventory inventory = item.getVariant().getInventory();
            int availableStock = inventory != null ? inventory.getStockQuantity() : 0;
            if (quantity > availableStock) {
                throw new InsufficientStockException("Only " + availableStock + " units available in store.");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return buildCartDto(cart);
    }

    @Transactional
    public CartDto removeCartItem(Long cartItemId) {
        return updateCartItemQuantity(cartItemId, 0);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public CartDto buildCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());

        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(mapper::toCartItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        int totalCount = 0;
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItemDto item : itemDtos) {
            totalCount += item.getQuantity();
            subtotal = subtotal.add(item.getItemTotal());
        }

        dto.setTotalItemsCount(totalCount);
        dto.setSubtotal(subtotal);
        dto.setFreeDeliveryThreshold(freeDeliveryThreshold);

        if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
            dto.setDeliveryCharge(BigDecimal.ZERO);
            dto.setFreeDelivery(false);
            dto.setGrandTotal(BigDecimal.ZERO);
            dto.setAmountNeededForFreeDelivery(freeDeliveryThreshold);
        } else if (subtotal.compareTo(freeDeliveryThreshold) >= 0) {
            dto.setDeliveryCharge(BigDecimal.ZERO);
            dto.setFreeDelivery(true);
            dto.setGrandTotal(subtotal);
            dto.setAmountNeededForFreeDelivery(BigDecimal.ZERO);
        } else {
            dto.setDeliveryCharge(standardDeliveryCharge);
            dto.setFreeDelivery(false);
            dto.setGrandTotal(subtotal.add(standardDeliveryCharge));
            dto.setAmountNeededForFreeDelivery(freeDeliveryThreshold.subtract(subtotal));
        }

        return dto;
    }
}
