package com.kirana.service;

import com.kirana.dto.CategoryDto;
import com.kirana.entity.Category;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private EntityDtoMapper mapper = new EntityDtoMapper();

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setNameEn("Dals & Pulses");
        testCategory.setNameTe("పప్పులు");
        testCategory.setSlug("dals-pulses");
        testCategory.setDisplayOrder(2);
        testCategory.setIsActive(true);
    }

    @Test
    @DisplayName("getActiveCategories should return active categories list sorted by display order")
    void testGetActiveCategories() {
        when(categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()).thenReturn(List.of(testCategory));

        List<CategoryDto> result = categoryService.getActiveCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dals & Pulses", result.get(0).getNameEn());
        assertEquals("పప్పులు", result.get(0).getNameTe());
    }

    @Test
    @DisplayName("createCategory should generate slug if absent and persist")
    void testCreateCategory() {
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            c.setId(20L);
            return c;
        });

        CategoryDto dto = new CategoryDto();
        dto.setNameEn("Spices & Masalas");
        dto.setNameTe("మసాలాలు");
        dto.setDisplayOrder(4);

        CategoryDto created = categoryService.createCategory(dto);

        assertNotNull(created);
        assertEquals(20L, created.getId());
        assertEquals("spices-masalas", created.getSlug());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
