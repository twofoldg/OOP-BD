package com.oop.bd.praktikum.controller;

import com.oop.bd.praktikum.dto.WarehouseDTO;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.service.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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

    public void updateWarehouse(String currentName, WarehouseDTO warehouseDTO) {
        Warehouse existingWarehouse = warehouseService.findWarehouseByName(currentName);
        existingWarehouse.setName(warehouseDTO.getName());
        warehouseService.save(existingWarehouse);
    }

    public void deleteWarehouse(String name) throws NotFoundException {
        Warehouse warehouse = warehouseService.findWarehouseByName(name);

        warehouseService.deleteById(warehouse.getId());
    }

    public WarehouseDTO getWarehouseByName(String warehouseName) {
        Warehouse warehouse = warehouseService.findWarehouseByName(warehouseName);
        return modelMapper.map(warehouse, WarehouseDTO.class);
    }
}
