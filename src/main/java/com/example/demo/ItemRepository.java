package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 두 번째 인자를 String에서 Integer로 변경하세요
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> { 
}