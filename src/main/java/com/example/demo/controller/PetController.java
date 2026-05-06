package com.example.demo.controller;

import com.example.demo.dto.InitialPetResponseDto;
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

    // 1. 최초 로그인 시 펫 생성 API (반환 타입을 InitialPetResponseDto로 수정)
    @PostMapping("/initial")
    public ResponseEntity<InitialPetResponseDto> createInitialPet(
            @RequestParam(name = "uid") String uid,
            @RequestParam(name = "name") String name) {
        InitialPetResponseDto response = petService.createInitialPet(uid, name);
        return ResponseEntity.ok(response);
    }

    // 2. 밥 먹이기 API
    @PostMapping("/{petId}/feed")
    public ResponseEntity<Pet> feedPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.feedPet(petId));
    }

    // 3. 잠재우기 API
    @PostMapping("/{petId}/sleep")
    public ResponseEntity<Pet> sleepPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.sleepPet(petId));
    }

    // 4. 놀아주기 API
    @PostMapping("/{petId}/play")
    public ResponseEntity<Pet> playWithPet(@PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.playWithPet(petId));
    }
}