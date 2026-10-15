package com.app.eventhive.service;

import com.app.eventhive.dto.CategoryResponse;
import com.app.eventhive.entity.Category;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public CategoryResponse createCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Category already exists: " + name);
        }
        Category category = categoryRepository.save(Category.builder().name(name).build());
        return new CategoryResponse(category.getId(), category.getName());
    }
}
