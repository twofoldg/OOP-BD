package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import java.util.List;

public interface ProductService {

  List<Product> findAll();

  Product findById(Long id);

  void save(Product product);

  void deleteById(Long id);

  Product findProductByName(String productName);

  List<Product> searchProducts(String productName, Integer quantity, String category, String warehouse);

  Product findProductByNameAndCategoryAndWarehouse(String productName, Category category, Warehouse warehouse);
}
