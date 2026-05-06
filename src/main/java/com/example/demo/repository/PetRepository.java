package com.example.demo.repository;

import com.example.demo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    // uid로 펫을 찾기
    Optional<Pet> findByUid(String uid); 
}