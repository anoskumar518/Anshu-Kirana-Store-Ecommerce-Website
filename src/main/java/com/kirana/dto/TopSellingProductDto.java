package com.kirana.dto;

import java.math.BigDecimal;

public class TopSellingProductDto {
    private String productName;
    private String unitName;
    private long totalQuantity;
    private BigDecimal totalRevenue;

    public TopSellingProductDto() {}

    public TopSellingProductDto(String productName, String unitName, long totalQuantity, BigDecimal totalRevenue) {
        this.productName = productName;
        this.unitName = unitName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(long totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
