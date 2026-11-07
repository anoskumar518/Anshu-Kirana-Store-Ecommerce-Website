package com.kirana.controller;

import com.kirana.dto.ApiResponse;
import com.kirana.dto.CreateOrderRequest;
import com.kirana.dto.OrderDto;
import com.kirana.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderService.placeOrder(request);
        return ResponseEntity.ok(ApiResponse.ok("Order placed successfully with Cash on Delivery!", order));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderById(id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Cancelled by customer";
        OrderDto order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled successfully", order));
    }
}
