package com.oop.bd.praktikum.repository;

import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
    JpaSpecificationExecutor<Product> {

  Optional<Product> findProductByName(String name);

  Optional<Product> findProductByNameAndCategoryAndWarehouse(String productName, Category category,
      Warehouse warehouse);
}
