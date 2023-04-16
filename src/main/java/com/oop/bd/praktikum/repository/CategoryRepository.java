package com.oop.bd.praktikum.repository;

import com.oop.bd.praktikum.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findCategoryByName(String name);
}
