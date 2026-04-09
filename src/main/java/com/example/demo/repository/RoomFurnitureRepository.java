package com.example.demo.repository;

import com.example.demo.entity.RoomFurniture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomFurnitureRepository extends JpaRepository<RoomFurniture, Long> {
}