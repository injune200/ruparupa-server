package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFurniture; // 추가
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomFurnitureRepository; // 추가
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final RoomRepository roomRepository;
    private final RoomFurnitureRepository roomFurnitureRepository; // 가구 배치를 위해 추가

    // 0. 최초 로그인 시 펫, 방, 기본 가구 3종 세트 자동 생성
    @Transactional
    public void createInitialSetup(User user) {
        String[] personalities = {"ACTIVE", "CALM", "LAZY"};
        String randomPersonality = personalities[new Random().nextInt(personalities.length)];

        Pet initialPet = Pet.builder()
                .user(user)
                .name("루파") 
                .personality(randomPersonality)
                .build();
        Pet savedPet = petRepository.save(initialPet);

        Room room = new Room();
        room.setPetId(savedPet.getId());
        room.setWallType("default_wall");
        room.setFloorTileType("default_tile");
        Room savedRoom = roomRepository.save(room);

        // 가구 3종 세트 자동 배치 (침대, 장난감 박스, 사료 봉투)
        RoomFurniture bed = new RoomFurniture();
        bed.setRoomId(savedRoom.getId());
        bed.setType("bed");
        bed.setX(2); bed.setY(3); bed.setDirection(0);

        RoomFurniture toyBox = new RoomFurniture();
        toyBox.setRoomId(savedRoom.getId());
        toyBox.setType("toy_box");
        toyBox.setX(5); toyBox.setY(3); toyBox.setDirection(0);

        RoomFurniture feedBag = new RoomFurniture();
        feedBag.setRoomId(savedRoom.getId());
        feedBag.setType("feed_bag");
        feedBag.setX(8); feedBag.setY(3); feedBag.setDirection(0);

        roomFurnitureRepository.saveAll(List.of(bed, toyBox, feedBag));
    }

    // 1. 밥 먹이기 (사료 봉투 상호작용)
    @Transactional
    public Pet feedPet(Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));
        
        // 상태 검증
        if ("SLEEPING".equals(pet.getCurrentAction())) {
            throw new IllegalStateException("펫이 자고 있어서 먹이를 줄 수 없습니다.");
        }
        if (pet.getHunger() >= 100) {
            throw new IllegalStateException("펫이 이미 배가 부릅니다.");
        }

        pet.setHunger(Math.min(pet.getHunger() + 30, 100));
        pet.setCurrentAction("EATING");
        return petRepository.save(pet);
    }

    // 2. 잠재우기 (침대 상호작용)
    @Transactional
    public Pet sleepPet(Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        // 상태 검증
        if ("SLEEPING".equals(pet.getCurrentAction())) {
            throw new IllegalStateException("펫이 이미 자고 있습니다.");
        }

        pet.setStamina(Math.min(pet.getStamina() + 30, 100));
        pet.setCurrentAction("SLEEPING");
        return petRepository.save(pet);
    }

    // 3. 놀아주기 (장난감 박스 상호작용)
    @Transactional
    public Pet playWithPet(Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        // 상태 검증
        if ("SLEEPING".equals(pet.getCurrentAction())) {
            throw new IllegalStateException("펫이 자고 있어서 놀 수 없습니다.");
        }
        if (pet.getStamina() < 20) {
            throw new IllegalStateException("스태미나가 부족하여 놀 수 없습니다.");
        }

        pet.setStamina(Math.max(pet.getStamina() - 20, 0));
        pet.setHunger(Math.max(pet.getHunger() - 10, 0)); 
        pet.setCurrentAction("PLAYING");
        return petRepository.save(pet);
    }
}