package com.example.demo.controller;

import com.example.demo.entity.Pet;
import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    // 1. 밥 먹이기 창구
    @GetMapping("/pet/feed") 
    public String feedPet(@RequestParam("petId") Long petId) {
        Pet updatedPet = petService.feedPet(petId);
        return "냠냠! " + updatedPet.getName() + "의 현재 포만감: " + updatedPet.getHunger();
    }

    // 2. 잠재우기 창구
    @GetMapping("/pet/sleep") 
    public String sleepPet(@RequestParam("petId") Long petId) {
        Pet updatedPet = petService.sleepPet(petId);
        return "쿨쿨... " + updatedPet.getName() + "의 현재 에너지: " + updatedPet.getEnergy();
    }
}