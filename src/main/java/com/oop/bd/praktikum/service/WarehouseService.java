package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.entity.Warehouse;

import java.util.List;

public interface WarehouseService {

    List<Warehouse> findAll();

    void save(Warehouse warehouse);

    void deleteByName(String name);

    Warehouse findWarehouseByName(String name);

    void updateWarehouse(String currentName, String newWarehouseName);
}
