package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.entity.Warehouse;

import java.util.List;

public interface WarehouseService {

    List<Warehouse> findAll();

    Warehouse findById(Long id);

    void save(Warehouse warehouse);

    void deleteById(Long id);

    Warehouse findWarehouseByName(String name);
}
