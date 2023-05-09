package com.oop.bd.praktikum.config;

import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.dto.ProductDTO;
import com.oop.bd.praktikum.dto.WarehouseDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    //This is used for mapping of the entities to DTOs and vice versa.
    @Bean
    public static ModelMapper configure() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.
                typeMap(Category.class, CategoryDTO.class)
                .addMapping(Category::getName, CategoryDTO::setName);

        modelMapper.typeMap(Product.class, ProductDTO.class)
                .addMapping(Product::getName, ProductDTO::setName)
                .addMapping(Product::getCategoryName, ProductDTO::setCategoryName)
                .addMapping(Product::getWarehouseName, ProductDTO::setWarehouseName)
                .addMapping(Product::getQuantity, ProductDTO::setQuantity);

        modelMapper.typeMap(Warehouse.class, WarehouseDTO.class)
                .addMapping(Warehouse::getName, WarehouseDTO::setName);

        modelMapper.typeMap(WarehouseDTO.class, Warehouse.class)
                .addMapping(WarehouseDTO::getName, Warehouse::setName);

        modelMapper.typeMap(ProductDTO.class, Product.class)
                .addMapping(ProductDTO::getName, Product::setName)
                .addMapping(ProductDTO::getCategoryName, Product::setCategory)
                .addMapping(ProductDTO::getWarehouseName, Product::setWarehouse)
                .addMapping(ProductDTO::getQuantity, Product::setQuantity);

        modelMapper.typeMap(CategoryDTO.class, Category.class)
                .addMapping(CategoryDTO::getName, Category::setName);

        return modelMapper;
    }
}
