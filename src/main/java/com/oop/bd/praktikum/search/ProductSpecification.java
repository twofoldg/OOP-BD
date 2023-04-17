package com.oop.bd.praktikum.search;

import com.oop.bd.praktikum.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

  public static Specification<Product> productNameContains(String name) {
    return (root, query, criteriaBuilder) -> {
      if (name == null || name.isEmpty()) {
        return criteriaBuilder.conjunction(); // return "1=1" condition when name is null
      }
      return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Product> quantityEquals(Integer quantity) {
    return (root, query, cb) -> {
      if (quantity == null) {
        return cb.conjunction(); // return "1=1" condition when quantity is null
      }
      return cb.equal(root.get("quantity"), quantity);
    };
  }

  public static Specification<Product> categoryEquals(String categoryName) {
    return (root, query, cb) -> {
      if (categoryName == null || categoryName.isEmpty()) {
        return cb.conjunction(); // return "1=1" condition when categoryName is null
      }
      return cb.equal(root.get("category").get("name"), categoryName);
    };
  }

  public static Specification<Product> warehouseEquals(String warehouseName) {
    return (root, query, cb) -> {
      if (warehouseName == null || warehouseName.isEmpty()) {
        return cb.conjunction(); // return "1=1" condition when warehouseName is null
      }
      return cb.equal(root.get("warehouse").get("name"), warehouseName);
    };
  }

}

