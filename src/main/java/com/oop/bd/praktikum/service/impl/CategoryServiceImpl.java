package com.oop.bd.praktikum.service.impl;

import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.repository.CategoryRepository;
import com.oop.bd.praktikum.service.CategoryService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

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
  public Category findById(Long id) throws NotFoundException {
    return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException());
  }

  @Override
  public Category save(Category category) {
    return categoryRepository.saveAndFlush(category);
  }

  @Override
  public void deleteById(Long id) throws NotFoundException {
    Category category = findById(id);
    categoryRepository.deleteById(category.getId());
  }

  @Override
  public Category findCategoryByName(String name) {
    return categoryRepository.findCategoryByName(name).orElseThrow();
  }
}
