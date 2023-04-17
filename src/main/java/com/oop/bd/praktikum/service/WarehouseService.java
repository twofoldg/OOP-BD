package com.oop.bd.praktikum.service;

import com.oop.bd.praktikum.entity.Warehouse;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public interface WarehouseService {

  List<Warehouse> findAll();

  Warehouse findById(Long id) throws NotFoundException;

  void save(Warehouse warehouse);

  void deleteById(Long id) throws NotFoundException;

  Warehouse findWarehouseByName(String name);
}
