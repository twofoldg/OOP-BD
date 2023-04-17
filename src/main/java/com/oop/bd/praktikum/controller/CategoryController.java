package com.oop.bd.praktikum.controller;

import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.service.CategoryService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Controller;

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
        .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
  }

  public void createCategory(CategoryDTO categoryDTO) {
    Category category = modelMapper.map(categoryDTO, Category.class);
    categoryService.createCategory(category);
  }

  public void updateCategory(String currentName, CategoryDTO categoryDTO) {
  categoryService.updateCategory(currentName, categoryDTO);
  }

  public void deleteCategory(String name) throws NotFoundException {
    Category category = categoryService.findCategoryByName(name);

    categoryService.deleteById(category.getId());
  }

  public CategoryDTO getCategoryByName(String categoryName) {
    Category category = categoryService.findCategoryByName(categoryName);
    return modelMapper.map(category, CategoryDTO.class);
  }
}
