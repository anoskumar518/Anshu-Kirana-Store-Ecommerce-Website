package com.kirana.mapper;

import com.kirana.dto.*;
import com.kirana.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getPreferredLanguage(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );
    }

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setNameEn(category.getNameEn());
        dto.setNameTe(category.getNameTe());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setIsActive(category.getIsActive());
        if (category.getProducts() != null) {
            dto.setProductCount(category.getProducts().size());
        }
        return dto;
    }

    public ProductVariantDto toVariantDto(ProductVariant variant) {
        if (variant == null) return null;
        ProductVariantDto dto = new ProductVariantDto();
        dto.setId(variant.getId());
        dto.setProductId(variant.getProduct() != null ? variant.getProduct().getId() : null);
        dto.setUnitName(variant.getUnitName());
        dto.setSku(variant.getSku());
        dto.setMrp(variant.getMrp());
        dto.setSellingPrice(variant.getSellingPrice());
        dto.setDiscountPercent(variant.getDiscountPercent());
        dto.setIsDefault(variant.getIsDefault());
        dto.setIsActive(variant.getIsActive());

        if (variant.getInventory() != null) {
            int stock = variant.getInventory().getStockQuantity();
            dto.setStockQuantity(stock);
            dto.setInStock(stock > 0);
            dto.setLowStock(stock <= variant.getInventory().getLowStockThreshold());
        } else {
            dto.setStockQuantity(0);
            dto.setInStock(false);
            dto.setLowStock(true);
        }
        return dto;
    }

    public ProductDto toProductDto(Product product) {
        if (product == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryNameEn(product.getCategory().getNameEn());
            dto.setCategoryNameTe(product.getCategory().getNameTe());
        }
        dto.setNameEn(product.getNameEn());
        dto.setNameTe(product.getNameTe());
        dto.setBrand(product.getBrand());
        dto.setDescriptionEn(product.getDescriptionEn());
        dto.setDescriptionTe(product.getDescriptionTe());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setIsActive(product.getIsActive());

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            dto.setVariants(product.getVariants().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                    .map(this::toVariantDto)
                    .collect(Collectors.toList()));

            // Find default variant or first active variant
            ProductVariant defaultVar = product.getVariants().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getIsActive()) && Boolean.TRUE.equals(v.getIsDefault()))
                    .findFirst()
                    .orElse(product.getVariants().get(0));
            dto.setDefaultVariant(toVariantDto(defaultVar));
        }

        return dto;
    }

    public CartItemDto toCartItemDto(CartItem item) {
        if (item == null) return null;
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        ProductVariant variant = item.getVariant();
        if (variant != null) {
            dto.setVariantId(variant.getId());
            dto.setUnitName(variant.getUnitName());
            dto.setUnitPrice(variant.getSellingPrice());
            dto.setMrp(variant.getMrp());
            dto.setItemTotal(variant.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            if (variant.getInventory() != null) {
                int stock = variant.getInventory().getStockQuantity();
                dto.setAvailableStock(stock);
                dto.setInStock(stock >= item.getQuantity());
            }

            Product product = variant.getProduct();
            if (product != null) {
                dto.setProductId(product.getId());
                dto.setProductNameEn(product.getNameEn());
                dto.setProductNameTe(product.getNameTe());
                dto.setBrand(product.getBrand());
                dto.setImageUrl(product.getImageUrl());
            }
        }
        return dto;
    }

    public OrderItemDto toOrderItemDto(OrderItem item) {
        if (item == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setVariantId(item.getVariantId());
        dto.setProductName(item.getProductName());
        dto.setUnitName(item.getUnitName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setProductImageUrl(item.getProductImageUrl());
        return dto;
    }

    public OrderDto toOrderDto(Order order) {
        if (order == null) return null;
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setDeliveryVillage(order.getDeliveryVillage());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryInstructions(order.getDeliveryInstructions());
        dto.setSubtotal(order.getSubtotal());
        dto.setDeliveryCharge(order.getDeliveryCharge());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCancellationReason(order.getCancellationReason());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList()));
        }

        return dto;
    }
}
