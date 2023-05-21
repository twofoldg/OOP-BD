package com.oop.bd.praktikum.service.impl;

import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.exceptions.ConflictException;
import com.oop.bd.praktikum.exceptions.NotFoundException;
import com.oop.bd.praktikum.repository.CategoryRepository;
import com.oop.bd.praktikum.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found!"));
    }

    @Override
    public void save(Category category) {
        categoryRepository.saveAndFlush(category);
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        Category category = findById(id);
        categoryRepository.deleteById(category.getId());
    }

    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findCategoryByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new NotFoundException("Category Not Found!"));
    }

    @Override
    public void updateCategory(String currentName, String newCategoryName) {
        Category existingCategory = findCategoryByName(currentName);
        existingCategory.setName(newCategoryName);
        save(existingCategory);
    }

    @Override
    public void createCategory(Category category) {
        final String categoryName = category.getName().trim();

        if (categoryRepository.findCategoryByNameIgnoreCase(categoryName).isPresent()) {
            throw new ConflictException(String.format("%s already exists!", categoryName));
        }

        save(category);
    }
}
