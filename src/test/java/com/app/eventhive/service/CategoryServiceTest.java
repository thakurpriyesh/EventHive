package com.app.eventhive.service;

import com.app.eventhive.dto.CategoryResponse;
import com.app.eventhive.entity.Category;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void testGetAllCategories() {
        Category c1 = Category.builder().id(1L).name("Concerts").build();
        Category c2 = Category.builder().id(2L).name("Conferences").build();
        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponse> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
        assertEquals("Concerts", result.get(0).name());
        assertEquals("Conferences", result.get(1).name());
    }

    @Test
    void testCreateCategorySuccess() {
        when(categoryRepository.existsByName("Sports")).thenReturn(false);
        Category saved = Category.builder().id(3L).name("Sports").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse result = categoryService.createCategory("Sports");
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Sports", result.name());
    }

    @Test
    void testCreateCategoryDuplicateThrowsBadRequest() {
        when(categoryRepository.existsByName("Sports")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory("Sports"));
        verify(categoryRepository, never()).save(any());
    }
}
