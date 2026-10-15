package com.app.eventhive.controller;

import com.app.eventhive.dto.CategoryResponse;
import com.app.eventhive.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryService);
    }

    @Test
    void testGetAllCategories() {
        when(categoryService.getAllCategories()).thenReturn(List.of(new CategoryResponse(1L, "Concerts")));

        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategories();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Concerts", response.getBody().get(0).name());
    }

    @Test
    void testCreateCategory() {
        when(categoryService.createCategory("Workshops")).thenReturn(new CategoryResponse(2L, "Workshops"));

        ResponseEntity<CategoryResponse> response = categoryController.createCategory("Workshops");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Workshops", response.getBody().name());
    }
}
