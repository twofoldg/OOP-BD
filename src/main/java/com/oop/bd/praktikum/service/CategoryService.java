package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    void save(Category category);

    void deleteByName(String name);

    Category findCategoryByName(String name);

    void updateCategory(String currentName, String newCategoryName);

    void createCategory(Category category);
}
