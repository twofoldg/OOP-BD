package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.entity.Category;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    void save(Category category);

    void deleteById(Long id) throws NotFoundException;

    Category findCategoryByName(String name);

    void updateCategory(String currentName, CategoryDTO categoryDTO);

    void createCategory(Category category);
}
