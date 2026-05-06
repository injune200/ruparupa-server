package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // 펫 ID로 해당 펫의 방을 찾는 기능
    Optional<Room> findByPetId(Long petId);
}