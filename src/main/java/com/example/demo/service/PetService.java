package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFurniture;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomFurnitureRepository;
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.MyPetResponseDto;

import java.util.List;
import java.util.Random;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomFurnitureRepository roomFurnitureRepository;

    // // 0. 최초 로그인 시 펫, 방, 기본 가구 3종 세트 자동 생성 (수정 없음)
    // @Transactional
    // public void createInitialSetup(User user) {
    //     String[] personalities = {"ACTIVE", "CALM", "LAZY"};
    //     String randomPersonality = personalities[new Random().nextInt(personalities.length)];

    //     Pet initialPet = Pet.builder()
    //             .user(user)
    //             .name("루파") 
    //             .personality(randomPersonality)
    //             .build();
    //     Pet savedPet = petRepository.save(initialPet);

    //     Room room = new Room();
    //     room.setPetId(savedPet.getId());
    //     room.setWallType("default_wall");
    //     room.setFloorTileType("default_tile");
    //     Room savedRoom = roomRepository.save(room);

    //     RoomFurniture bed = new RoomFurniture();
    //     bed.setRoomId(savedRoom.getId());
    //     bed.setType("bed");
    //     bed.setX(2); bed.setY(3); bed.setDirection(0);

    //     RoomFurniture toyBox = new RoomFurniture();
    //     toyBox.setRoomId(savedRoom.getId());
    //     toyBox.setType("toy_box");
    //     toyBox.setX(5); toyBox.setY(3); toyBox.setDirection(0);

    //     RoomFurniture feedBag = new RoomFurniture();
    //     feedBag.setRoomId(savedRoom.getId());
    //     feedBag.setType("feed_bag");
    //     feedBag.setX(8); feedBag.setY(3); feedBag.setDirection(0);

    //     roomFurnitureRepository.saveAll(List.of(bed, toyBox, feedBag));
    // }

    @Transactional
    public MyPetResponseDto getOrCreatePet(String currentUid) {
        User user = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Pet pet = petRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialSetupAndReturnPet(user));

        return convertToDto(pet);
    }

    private MyPetResponseDto convertToDto(Pet pet) {
        return MyPetResponseDto.builder()
                .petId(pet.getPetUid())
                .ownerUserId(pet.getUser().getUid())
                .name(pet.getName())
                .characterAssetKey(pet.getCharacterAssetKey())
                .personality(pet.getPersonality())
                .equippedItemIds(pet.getEquippedItemIds()) // 리스트 그대로 반환
                .updatedAt(pet.getUpdatedAt())
                .status(MyPetResponseDto.StatusDto.builder()
                        .satiety(pet.getHunger())
                        .vitality(pet.getStamina())
                        .isEgg(pet.isEgg())
                        .isSleep(pet.isSleep()) // 필드 직접 매핑
                        .build())
                .build();
    }

    @Transactional
    public Pet createInitialSetupAndReturnPet(User user) {
        String[] personalities = {"ACTIVE", "CALM", "LAZY"};
        String randomPersonality = personalities[new Random().nextInt(personalities.length)];

        Pet initialPet = Pet.builder()
                .user(user)
                .name("루파")
                .characterAssetKey("room/characters/lupa_default")
                .personality(randomPersonality)
                .hunger(100)
                .stamina(100)
                .isEgg(true)
                .isSleep(false) // 초기값 설정
                .equippedItemIds(Collections.emptyList()) // 초기 아이템 없음
                .build();
                
        initialPet.generatePetUid(user.getUid());
        Pet savedPet = petRepository.save(initialPet);

        // 방 및 가구 세팅 로직 유지
        Room room = new Room();
        room.setPetId(savedPet.getId());
        room.setWallType("default_wall");
        room.setFloorTileType("default_tile");
        Room savedRoom = roomRepository.save(room);

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
        
        return savedPet;
    }

    // 1. 밥 먹이기 (사료 봉투 상호작용)
    @Transactional
    public Pet feedPet(String currentUid, Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));
        
        // 소유권 검증 로직
        if (!pet.getUser().getUid().equals(currentUid)) {
            throw new IllegalArgumentException("본인의 펫만 조작할 수 있습니다.");
        }

        // 상태 검증
        if (pet.isSleep()) throw new IllegalStateException("펫이 자고 있습니다.");

        if (pet.getHunger() >= 100) {
            throw new IllegalStateException("펫이 이미 배가 부릅니다.");
        }

        pet.setHunger(Math.min(pet.getHunger() + 30, 100));
        return petRepository.save(pet);
    }

    // 2. 잠재우기 (침대 상호작용)
    @Transactional
    public Pet sleepPet(String currentUid, Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));
        
        // 소유권 검증 로직
        if (!pet.getUser().getUid().equals(currentUid)) {
            throw new IllegalArgumentException("본인의 펫만 조작할 수 있습니다.");
        }

        // 상태 검증
        if (pet.isSleep()) {
            throw new IllegalStateException("펫이 이미 자고 있습니다.");
        }

        pet.setStamina(Math.min(pet.getStamina() + 30, 100));
        return petRepository.save(pet);
    }

    // 3. 놀아주기 (장난감 박스 상호작용)
    @Transactional
    public Pet playWithPet(String currentUid, Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));
        
        // 소유권 검증 로직
        if (!pet.getUser().getUid().equals(currentUid)) {
            throw new IllegalArgumentException("본인의 펫만 조작할 수 있습니다.");
        }

        // 상태 검증
        if (pet.isSleep()) {
            throw new IllegalStateException("펫이 자고 있어서 놀 수 없습니다.");
        }
        if (pet.getStamina() < 20) {
            throw new IllegalStateException("스태미나가 부족하여 놀 수 없습니다.");
        }

        pet.setStamina(Math.max(pet.getStamina() - 20, 0));
        pet.setHunger(Math.max(pet.getHunger() - 10, 0)); 
        return petRepository.save(pet);
    }
}