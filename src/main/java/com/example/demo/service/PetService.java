package com.example.demo.service;

import com.example.demo.entity.Pet;
import com.example.demo.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    // 1. 밥 먹이기 계산 로직
    @Transactional
    public Pet feedPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        int recoveryAmount = 30;
        int currentHunger = pet.getHunger();
        int nextHunger = Math.min(currentHunger + recoveryAmount, 100);
        
        pet.setHunger(nextHunger);
        pet.setCurrentAction("EATING"); 
        
        return pet;
    }

    // 2. 잠재우기 계산 로직
    @Transactional
    public Pet sleepPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        // 수면 시 에너지(피로도) 회복
        int recoveryAmount = 30;
        int currentEnergy = pet.getEnergy();
        
        // 최대 100을 넘지 않게 처리
        int nextEnergy = Math.min(currentEnergy + recoveryAmount, 100);
        
        pet.setEnergy(nextEnergy);
        pet.setCurrentAction("SLEEPING"); // 수면 중으로 상태 변경
        
        return pet;
    }
}