package com.oop.bd.praktikum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
    private String name;
    private int quantity;
    private String warehouseName;
    private String categoryName;
}
