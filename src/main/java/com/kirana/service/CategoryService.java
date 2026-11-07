package com.kirana.service;

import com.kirana.dto.CategoryDto;
import com.kirana.entity.Category;
import com.kirana.exception.BadRequestException;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityDtoMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, EntityDtoMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<CategoryDto> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getAllCategoriesForAdmin() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapper.toCategoryDto(category);
    }

    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return mapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        String slug = dto.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = dto.getNameEn().toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
        }

        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = new Category();
        category.setNameEn(dto.getNameEn());
        category.setNameTe(dto.getNameTe());
        category.setSlug(slug);
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setNameEn(dto.getNameEn());
        category.setNameTe(dto.getNameTe());
        category.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) category.setImageUrl(dto.getImageUrl());
        if (dto.getDisplayOrder() != null) category.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getIsActive() != null) category.setIsActive(dto.getIsActive());

        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot delete category containing products. Deactivate it instead.");
        }

        categoryRepository.delete(category);
    }
}
