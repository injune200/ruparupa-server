package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 주의: Room 엔티티의 ID를 String으로 변경했다면 제네릭 타입도 <Room, String>으로 변경해야 합니다.
public interface RoomRepository extends JpaRepository<Room, String> { 
    
    // 기존: 펫 ID로 해당 펫의 방을 찾는 기능
    // Optional<Room> findByPetId(Long petId);

    //방 주인 ID(uID)로 방을 찾는 기능 
    Optional<Room> findByOwnerUserId(String ownerUserId);
}