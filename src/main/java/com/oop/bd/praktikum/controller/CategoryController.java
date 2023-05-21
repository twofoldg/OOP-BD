package com.oop.bd.praktikum.controller;

import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryService.findAll();

        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
    }

    public void createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        categoryService.createCategory(category);
    }

    public void updateCategory(String currentName, String newCategoryName) {
        categoryService.updateCategory(currentName, newCategoryName);
    }

    public void deleteCategory(String name) {
        Category category = categoryService.findCategoryByName(name);

        categoryService.deleteById(category.getId());
    }
}
