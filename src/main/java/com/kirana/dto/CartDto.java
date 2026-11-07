package com.kirana.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
    private Long id;
    private List<CartItemDto> items = new ArrayList<>();
    private int totalItemsCount = 0;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private BigDecimal freeDeliveryThreshold = BigDecimal.valueOf(299.00);
    private boolean isFreeDelivery = false;
    private BigDecimal amountNeededForFreeDelivery = BigDecimal.ZERO;

    public CartDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }

    public int getTotalItemsCount() { return totalItemsCount; }
    public void setTotalItemsCount(int totalItemsCount) { this.totalItemsCount = totalItemsCount; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public BigDecimal getFreeDeliveryThreshold() { return freeDeliveryThreshold; }
    public void setFreeDeliveryThreshold(BigDecimal freeDeliveryThreshold) { this.freeDeliveryThreshold = freeDeliveryThreshold; }

    public boolean isFreeDelivery() { return isFreeDelivery; }
    public void setFreeDelivery(boolean freeDelivery) { isFreeDelivery = freeDelivery; }

    public BigDecimal getAmountNeededForFreeDelivery() { return amountNeededForFreeDelivery; }
    public void setAmountNeededForFreeDelivery(BigDecimal amountNeededForFreeDelivery) { this.amountNeededForFreeDelivery = amountNeededForFreeDelivery; }
}
