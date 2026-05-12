package com.example.roomlook.repository;

import com.example.roomlook.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByOwnerUserId(Long ownerUserId);
}
