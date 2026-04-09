package com.example.demo.repository;

import com.example.demo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<어떤엔티티, 기본키타입>
public interface PetRepository extends JpaRepository<Pet, Long> {
}