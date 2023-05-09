package com.oop.bd.praktikum.service.impl;

import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.exceptions.NotFoundException;
import com.oop.bd.praktikum.repository.WarehouseRepository;
import com.oop.bd.praktikum.service.WarehouseService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> new NotFoundException("Warehouse Not Found!"));
    }

    @Override
    public void save(Warehouse warehouse) {
        warehouseRepository.saveAndFlush(warehouse);
    }

    @Override
    public void deleteById(Long id) {
        Warehouse warehouse = findById(id);
        warehouseRepository.deleteById(warehouse.getId());
    }

    @Override
    public Warehouse findWarehouseByName(String name) {
        return warehouseRepository.findWarehouseByName(name)
                .orElseThrow(() -> new com.oop.bd.praktikum.exceptions.NotFoundException("Warehouse Not Found!"));
    }
}
