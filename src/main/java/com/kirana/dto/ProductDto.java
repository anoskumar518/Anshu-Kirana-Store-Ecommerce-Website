package com.kirana.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductDto {
    private Long id;
    private Long categoryId;
    private String categoryNameEn;
    private String categoryNameTe;
    private String nameEn;
    private String nameTe;
    private String brand;
    private String descriptionEn;
    private String descriptionTe;
    private String imageUrl;
    private Boolean isFeatured;
    private Boolean isActive;
    private List<ProductVariantDto> variants = new ArrayList<>();
    private ProductVariantDto defaultVariant;

    public ProductDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryNameEn() { return categoryNameEn; }
    public void setCategoryNameEn(String categoryNameEn) { this.categoryNameEn = categoryNameEn; }

    public String getCategoryNameTe() { return categoryNameTe; }
    public void setCategoryNameTe(String categoryNameTe) { this.categoryNameTe = categoryNameTe; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameTe() { return nameTe; }
    public void setNameTe(String nameTe) { this.nameTe = nameTe; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public String getDescriptionTe() { return descriptionTe; }
    public void setDescriptionTe(String descriptionTe) { this.descriptionTe = descriptionTe; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<ProductVariantDto> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDto> variants) { this.variants = variants; }

    public ProductVariantDto getDefaultVariant() { return defaultVariant; }
    public void setDefaultVariant(ProductVariantDto defaultVariant) { this.defaultVariant = defaultVariant; }
}
