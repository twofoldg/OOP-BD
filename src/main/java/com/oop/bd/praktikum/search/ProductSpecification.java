package com.oop.bd.praktikum.search;

import com.oop.bd.praktikum.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    // Specification for filtering products based on name
    public static Specification<Product> productNameContains(String name) {
        return (root, query, criteriaBuilder) -> {

            // If the name is null or empty, return a "1=1" condition (always true)
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Perform a case-insensitive LIKE search on the product name
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    // Specification for filtering products based on quantity
    public static Specification<Product> quantityEquals(Integer quantity) {
        return (root, query, cb) -> {

            // If the quantity is null, return a "1=1" condition (always true)
            if (quantity == null) {
                return cb.conjunction();
            }

            // Filter products with the specified quantity
            return cb.equal(root.get("quantity"), quantity);
        };
    }

    // Specification for filtering products based on category name
    public static Specification<Product> categoryEquals(String categoryName) {
        return (root, query, cb) -> {

            // If the categoryName is null or empty, return a "1=1" condition (always true)
            if (categoryName == null || categoryName.isEmpty()) {
                return cb.conjunction();
            }

            // Filter products with the specified category name
            return cb.equal(root.get("category").get("name"), categoryName);
        };
    }

    // Specification for filtering products based on warehouse name
    public static Specification<Product> warehouseEquals(String warehouseName) {
        return (root, query, cb) -> {

            // If the warehouseName is null or empty, return a "1=1" condition (always true)
            if (warehouseName == null || warehouseName.isEmpty()) {
                return cb.conjunction();
            }
            // Filter products with the specified warehouse name
            return cb.equal(root.get("warehouse").get("name"), warehouseName);
        };
    }
}

