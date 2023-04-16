package com.oop.bd.praktikum.repository;

import com.oop.bd.praktikum.entity.Warehouse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

  Optional<Warehouse> findWarehouseByName(String name);
}
