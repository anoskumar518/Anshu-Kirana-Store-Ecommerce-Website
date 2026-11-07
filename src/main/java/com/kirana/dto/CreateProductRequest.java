package com.kirana.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CreateProductRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Product name in English is required")
    private String nameEn;

    @NotBlank(message = "Product name in Telugu is required")
    private String nameTe;

    @NotBlank(message = "Brand is required")
    private String brand;

    private String descriptionEn;
    private String descriptionTe;
    private String imageUrl;
    private Boolean isFeatured = false;
    private Boolean isActive = true;

    private List<CreateVariantRequest> variants = new ArrayList<>();

    public CreateProductRequest() {}

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

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

    public List<CreateVariantRequest> getVariants() { return variants; }
    public void setVariants(List<CreateVariantRequest> variants) { this.variants = variants; }
}
