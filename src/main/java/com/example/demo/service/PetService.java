package com.example.demo.service;

import com.example.demo.dto.InitialPetResponseDto;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Room;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final RoomRepository roomRepository;
    private final Random random = new Random();

    // 0. 최초 로그인 시 펫 & 방 동시 생성 (InitialPetResponseDto 반환으로 변경)
    @Transactional
    public InitialPetResponseDto createInitialPet(String uid, String name) {
        Pet pet = new Pet();
        pet.setUid(uid);
        pet.setName(name);

        // 외형 랜덤 구현
        pet.setHeadSize(random.nextInt(5) + 1);
        pet.setBodySize(random.nextInt(5) + 1);
        pet.setEyeDesign(random.nextInt(5) + 1);
        pet.setNoseDesign(random.nextInt(5) + 1);
        pet.setMouthDesign(random.nextInt(5) + 1);

        // 성격 랜덤 지정
        String[] personalities = {"활발", "차분", "게으름"};
        pet.setPersonality(personalities[random.nextInt(personalities.length)]);

        // 기본 스탯 및 속성 부여
        pet.setHunger(100);
        pet.setEnergy(100);
        pet.setCleanliness(100);
        pet.setHappiness(100);
        pet.setEgg(true);
        pet.setDecoration("None");
        pet.setCurrentAction("IDLE");

        // 펫 저장
        Pet savedPet = petRepository.save(pet);

        // 방 생성 및 저장 
        Room room = new Room();
        room.setPetId(savedPet.getId());
        room.setWallType("default_wall");       // 벽지
        room.setFloorTileType("default_tile");  // 기본 타일
        Room savedRoom = roomRepository.save(room);

        // 펫과 방 정보를 한 번에 반환
        return new InitialPetResponseDto(savedPet, savedRoom);
    }

    // 1. 밥 먹이기
    @Transactional
    public Pet feedPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        pet.setHunger(Math.min(pet.getHunger() + 30, 100));
        pet.setCurrentAction("EATING");
        return pet;
    }

    // 2. 잠재우기
    @Transactional
    public Pet sleepPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        pet.setEnergy(Math.min(pet.getEnergy() + 30, 100));
        pet.setCurrentAction("SLEEPING");
        return pet;
    }

    // 3. 놀아주기
    @Transactional
    public Pet playWithPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        pet.setEnergy(Math.max(pet.getEnergy() - 20, 0));
        pet.setCurrentAction("PLAYING");
        return pet;
    }
}