package com.example.demo.repository;

import com.example.demo.entity.RoomFurniture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomFurnitureRepository extends JpaRepository<RoomFurniture, Long> {
    
    // 특정 방(roomId)에 배치된 가구들만 리스트로 찾아오기
    List<RoomFurniture> findByRoomId(Long roomId);
}