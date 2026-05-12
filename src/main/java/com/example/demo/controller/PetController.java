package com.example.demo.controller;

import com.example.demo.entity.Pet;
import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    // 1. 밥 먹이기 API
    @PostMapping("/{petId}/feed")
    public ResponseEntity<Pet> feedPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.feedPet(petId));
    }

    // 2. 잠재우기 API
    @PostMapping("/{petId}/sleep")
    public ResponseEntity<Pet> sleepPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.sleepPet(petId));
    }

    // 3. 놀아주기 API
    @PostMapping("/{petId}/play")
    public ResponseEntity<Pet> playWithPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.playWithPet(petId));
    }
}