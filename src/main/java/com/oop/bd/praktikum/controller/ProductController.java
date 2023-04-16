package com.oop.bd.praktikum.controller;

import com.oop.bd.praktikum.dto.ProductDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.service.CategoryService;
import com.oop.bd.praktikum.service.ProductService;
import com.oop.bd.praktikum.service.WarehouseService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProductController {

  private final ProductService productService;
  private final CategoryService categoryService;
  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @Autowired
  public ProductController(ProductService productService,
      CategoryService categoryService,
      WarehouseService warehouseService,
      ModelMapper modelMapper) {
    this.productService = productService;
    this.categoryService = categoryService;
    this.warehouseService = warehouseService;
    this.modelMapper = modelMapper;
  }

  public void createProduct(ProductDTO productDTO) {
    Product product = modelMapper.map(productDTO, Product.class);
    Category category = categoryService.findCategoryByName(productDTO.getCategoryName());
    Warehouse warehouse = warehouseService.findWarehouseByName(productDTO.getWarehouseName());
    product.setCategory(category);
    product.setWarehouse(warehouse);
    Product newProduct = productService.save(product);
    modelMapper.map(newProduct, ProductDTO.class);
  }

  public List<ProductDTO> getAllProducts() {
    return productService.findAll().stream()
        .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
  }

  public ProductDTO updateProductByName(String name, ProductDTO productDTO) {
    Product existingProduct = productService.findProductByName(name);
    Category category = categoryService.findCategoryByName(productDTO.getCategoryName());
    Warehouse warehouse = warehouseService.findWarehouseByName(productDTO.getWarehouseName());

    existingProduct.setName(productDTO.getName());
    existingProduct.setCategory(category);
    existingProduct.setWarehouse(warehouse);
    existingProduct.setQuantity(productDTO.getQuantity());
    productService.save(existingProduct);
    return modelMapper.map(existingProduct, ProductDTO.class);
  }

  public void deleteProduct(Long id) {
    productService.deleteById(id);
  }

  public List<ProductDTO> searchProducts(String searchName, Integer searchQuantity,
      String searchCategory, String searchWarehouse) {

    return productService.searchProducts(searchName, searchQuantity, searchCategory,
            searchWarehouse).stream().map(product -> modelMapper.map(product, ProductDTO.class))
        .toList();
  }

  public ProductDTO getProductByName(String productName) {
    Product product = productService.findProductByName(productName);
    return modelMapper.map(product, ProductDTO.class);
  }
}
