package com.oop.bd.praktikum.controller;

import com.oop.bd.praktikum.dto.WarehouseDTO;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final ModelMapper modelMapper;

    public WarehouseController(WarehouseService warehouseService, ModelMapper modelMapper) {
        this.warehouseService = warehouseService;
        this.modelMapper = modelMapper;
    }

    public List<WarehouseDTO> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.findAll();

        return warehouses.stream()
                .map(warehouse -> modelMapper.map(warehouse, WarehouseDTO.class))
                .toList();
    }

    public void createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = modelMapper.map(warehouseDTO, Warehouse.class);
        warehouseService.save(warehouse);
    }

    public void updateWarehouse(String currentName, String newWarehouseName) {
        warehouseService.updateWarehouse(currentName, newWarehouseName);
    }

    public void deleteWarehouse(String name) {
        warehouseService.deleteByName(name);
    }
}
