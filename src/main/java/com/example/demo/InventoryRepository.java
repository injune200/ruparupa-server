package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    
    // 유저와 아이템 ID로 인벤토리를 찾는 메서드
    Optional<Inventory> findByUserAndItemId(User user, int itemId);

    // 특정 유저의 모든 인벤토리 목록을 가져오는 메서드
    List<Inventory> findByUser(User user);
}