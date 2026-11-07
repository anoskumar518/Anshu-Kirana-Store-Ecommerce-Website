package com.kirana.controller;

import com.kirana.dto.AddToCartRequest;
import com.kirana.dto.ApiResponse;
import com.kirana.dto.CartDto;
import com.kirana.dto.UpdateCartItemRequest;
import com.kirana.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart() {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCartDto()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(@Valid @RequestBody AddToCartRequest request) {
        CartDto cart = cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartDto cart = cartService.updateCartItemQuantity(id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@PathVariable Long id) {
        CartDto cart = cartService.removeCartItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }
}
