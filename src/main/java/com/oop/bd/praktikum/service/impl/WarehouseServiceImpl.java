package com.oop.bd.praktikum.service.impl;

import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.repository.WarehouseRepository;
import com.oop.bd.praktikum.service.WarehouseService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

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
  public Warehouse findById(Long id) throws NotFoundException {
    return warehouseRepository.findById(id).orElseThrow(() -> new NotFoundException());
  }

  @Override
  public Warehouse save(Warehouse warehouse) {
    return warehouseRepository.saveAndFlush(warehouse);
  }

  @Override
  public void deleteById(Long id) throws NotFoundException {
    Warehouse warehouse = findById(id);
    warehouseRepository.deleteById(warehouse.getId());
  }

  @Override
  public Warehouse findWarehouseByName(String name) {
    return warehouseRepository.findWarehouseByName(name).orElseThrow();
  }
}
