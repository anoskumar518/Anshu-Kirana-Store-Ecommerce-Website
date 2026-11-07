package com.kirana.dto;

public class LowStockDto {
    private Long variantId;
    private Long productId;
    private String productName;
    private String unitName;
    private String sku;
    private int stockQuantity;
    private int lowStockThreshold;

    public LowStockDto() {}

    public LowStockDto(Long variantId, Long productId, String productName, String unitName, String sku, int stockQuantity, int lowStockThreshold) {
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.unitName = unitName;
        this.sku = sku;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
}
