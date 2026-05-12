package com.example.demo.repository;

import com.example.demo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // 

public interface PetRepository extends JpaRepository<Pet, Long> {
    
    // 오프라인 동기화를 위해 유저 ID로 펫을 찾는 기능 추가
    Optional<Pet> findByUserId(Long userId);
}