package com.kirana.service;

import com.kirana.dto.*;
import com.kirana.entity.*;
import com.kirana.exception.BadRequestException;
import com.kirana.exception.InsufficientStockException;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    private final EntityDtoMapper mapper;

    @Value("${app.store.free-delivery-threshold:299.00}")
    private BigDecimal freeDeliveryThreshold = BigDecimal.valueOf(299.00);

    @Value("${app.store.delivery-charge:20.00}")
    private BigDecimal standardDeliveryCharge = BigDecimal.valueOf(20.00);

    private final Random random = new Random();

    public OrderService(OrderRepository orderRepository,
                        CartService cartService,
                        CartRepository cartRepository,
                        InventoryRepository inventoryRepository,
                        StockMovementRepository stockMovementRepository,
                        PaymentRepository paymentRepository,
                        AuthService authService,
                        EntityDtoMapper mapper) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.paymentRepository = paymentRepository;
        this.authService = authService;
        this.mapper = mapper;
    }

    @Transactional
    public OrderDto placeOrder(CreateOrderRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        Cart cart = cartService.getOrCreateCartForCurrentUser();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your shopping cart is empty. Please add items before checkout.");
        }

        // 1. Generate unique human-readable Order Number: AKS-YYYYMMDD-XXXX
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%04d", random.nextInt(10000));
        String orderNumber = "AKS-" + datePart + "-" + randomSuffix;

        // 2. Recalculate all amounts from database to avoid client tampering
        BigDecimal calculatedSubtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setCustomerName(request.getCustomerName().trim());
        order.setCustomerPhone(request.getCustomerPhone().trim());
        order.setDeliveryVillage(request.getDeliveryVillage().trim());

        String fullAddress = request.getStreetArea().trim();
        if (request.getLandmark() != null && !request.getLandmark().isBlank()) {
            fullAddress += " (Near: " + request.getLandmark().trim() + ")";
        }
        order.setDeliveryAddress(fullAddress);
        order.setDeliveryInstructions(request.getDeliveryInstructions());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.COD);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PLACED);

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            int qty = cartItem.getQuantity();

            // Validate inventory
            Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for " + variant.getProduct().getNameEn()));

            if (inventory.getStockQuantity() < qty) {
                throw new InsufficientStockException("Insufficient stock for " + variant.getProduct().getNameEn() +
                        " (" + variant.getUnitName() + "). Available: " + inventory.getStockQuantity() + ", Requested: " + qty);
            }

            // Snapshot price
            BigDecimal unitPrice = variant.getSellingPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            calculatedSubtotal = calculatedSubtotal.add(itemSubtotal);

            OrderItem orderItem = new OrderItem(
                    order,
                    variant.getProduct().getId(),
                    variant.getId(),
                    variant.getProduct().getNameEn() + " / " + variant.getProduct().getNameTe(),
                    variant.getUnitName(),
                    unitPrice,
                    qty,
                    itemSubtotal,
                    variant.getProduct().getImageUrl()
            );
            orderItems.add(orderItem);

            // Deduct inventory atomically
            int newBalance = inventory.getStockQuantity() - qty;
            inventory.setStockQuantity(newBalance);
            inventoryRepository.save(inventory);

            // Record stock movement
            StockMovement movement = new StockMovement(
                    variant,
                    StockMovementType.SALE,
                    -qty,
                    newBalance,
                    orderNumber,
                    "Order placed by " + request.getCustomerName()
            );
            stockMovementRepository.save(movement);
        }

        order.setItems(orderItems);
        order.setSubtotal(calculatedSubtotal);

        // Calculate delivery charge
        BigDecimal deliveryCharge = calculatedSubtotal.compareTo(freeDeliveryThreshold) >= 0 ? BigDecimal.ZERO : standardDeliveryCharge;
        order.setDeliveryCharge(deliveryCharge);

        // Discount
        BigDecimal discount = BigDecimal.ZERO;
        order.setDiscountAmount(discount);

        // Grand Total
        order.setTotalAmount(calculatedSubtotal.add(deliveryCharge).subtract(discount));

        Order savedOrder = orderRepository.save(order);

        // Create Payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(savedOrder.getTotalAmount());
        payment.setPaymentMethod(savedOrder.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setRemarks("Payment due on Cash on Delivery");
        paymentRepository.save(payment);

        // Clear cart after successful checkout
        cartService.clearCart(cart);

        return mapper.toOrderDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrders() {
        User user = authService.getCurrentAuthenticatedUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(mapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        User currentUser = authService.getCurrentAuthenticatedUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);

        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to view this order");
        }

        return mapper.toOrderDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return mapper.toOrderDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        User currentUser = authService.getCurrentAuthenticatedUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);

        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to cancel this order");
        }

        // Only PLACED or CONFIRMED orders can be cancelled by customers
        if (!isAdmin && order.getOrderStatus() != OrderStatus.PLACED && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled in status: " + order.getOrderStatus() + ". Please call store support.");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Delivered orders cannot be cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason != null ? reason : "Cancelled by " + (isAdmin ? "Admin" : "Customer"));

        // Restock inventory for cancelled items
        for (OrderItem item : order.getItems()) {
            if (item.getVariantId() != null) {
                inventoryRepository.findByVariantId(item.getVariantId()).ifPresent(inventory -> {
                    int restored = inventory.getStockQuantity() + item.getQuantity();
                    inventory.setStockQuantity(restored);
                    inventoryRepository.save(inventory);

                    StockMovement movement = new StockMovement(
                            inventory.getVariant(),
                            StockMovementType.CANCELLATION_RESTOCK,
                            item.getQuantity(),
                            restored,
                            order.getOrderNumber(),
                            "Restocked due to order cancellation"
                    );
                    stockMovementRepository.save(movement);
                });
            }
        }

        return mapper.toOrderDto(orderRepository.save(order));
    }

    // Admin Operations
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getAllOrdersForAdmin(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage;

        if (status != null) {
            orderPage = orderRepository.findByOrderStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            orderPage = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<OrderDto> content = orderPage.getContent().stream()
                .map(mapper::toOrderDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        OrderStatus newStatus = request.getStatus();

        if (newStatus == OrderStatus.CANCELLED) {
            return cancelOrder(id, request.getCancellationReason());
        }

        if (newStatus == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentRepository.findByOrderId(order.getId()).forEach(p -> {
                p.setStatus(PaymentStatus.COMPLETED);
                p.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(p);
            });
        }

        order.setOrderStatus(newStatus);
        return mapper.toOrderDto(orderRepository.save(order));
    }
}
