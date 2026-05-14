package com.example.demo.controller;

import com.example.demo.dto.MyPetResponseDto;
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
    public ResponseEntity<Pet> feedPet(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.feedPet(currentUid, petId));
    }

    // 2. 잠재우기 API
    @PostMapping("/{petId}/sleep")
    public ResponseEntity<Pet> sleepPet(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.sleepPet(currentUid, petId));
    }

    // 3. 놀아주기 API
    @PostMapping("/{petId}/play")
    public ResponseEntity<Pet> playWithPet(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable(name = "petId") Long petId) {
        return ResponseEntity.ok(petService.playWithPet(currentUid, petId));
    }

    // 내 펫 정보 조회 (없으면 생성)
    @GetMapping("/me")
    public ResponseEntity<MyPetResponseDto> getMyPet(
            @RequestAttribute("currentUid") String currentUid) {
        return ResponseEntity.ok(petService.getOrCreatePet(currentUid));
    }
}