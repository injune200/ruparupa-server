package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    List<Inventory> findByUser(User user);
    Optional<Inventory> findByUserAndItemId(User user, String itemId);
}