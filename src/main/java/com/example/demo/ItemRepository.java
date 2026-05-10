package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    // 기본적으로 제공되는 findById(itemId)를 사용하여 조회합니다.
}