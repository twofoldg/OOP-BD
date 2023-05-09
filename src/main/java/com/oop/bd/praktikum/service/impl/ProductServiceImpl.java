package com.oop.bd.praktikum.service.impl;

import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.exceptions.NotFoundException;
import com.oop.bd.praktikum.repository.CategoryRepository;
import com.oop.bd.praktikum.repository.ProductRepository;
import com.oop.bd.praktikum.search.ProductSpecification;
import com.oop.bd.praktikum.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found!"));
    }

    @Override
    public void save(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() == null) {
            categoryRepository.save(product.getCategory());
        }
        productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = findById(id);
        productRepository.deleteById(product.getId());
    }

    @Override
    public Product findProductByName(String productName) {
        return productRepository.findProductByNameIgnoreCase(productName)
                .orElseThrow(() -> new NotFoundException("Product Not Found!"));
    }

    @Override
    public List<Product> searchProducts(String name, Integer quantity, String categoryName, String warehouseName) {
        Specification<Product> spec = Specification
                .where(ProductSpecification.productNameContains(name))
                .and(ProductSpecification.quantityEquals(quantity))
                .and(ProductSpecification.categoryEquals(categoryName))
                .and(ProductSpecification.warehouseEquals(warehouseName));

        return productRepository.findAll(spec);
    }

    @Override
    public Product findProductByNameAndCategoryAndWarehouse(String productName, Category category,
                                                            Warehouse warehouse) {
        return productRepository.
                findProductByNameIgnoreCaseAndCategoryAndWarehouse(productName, category, warehouse)
                .orElseThrow(() -> new NotFoundException("Product Not Found!"));
    }
}
