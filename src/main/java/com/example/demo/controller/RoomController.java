package com.example.demo.controller;

import com.example.demo.dto.RoomResponseDto;
import com.example.demo.entity.Pet;
import com.example.demo.entity.RoomFurniture;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomFurnitureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor // Repository들을 자동으로 연결(주입)해 주는 마법의 어노테이션입니다.
public class RoomController {

    // 점원이 사용할 창고(DB) 열쇠를 쥐어줍니다.
    private final PetRepository petRepository;
    private final RoomFurnitureRepository furnitureRepository;

    @GetMapping("/room")
    public RoomResponseDto getRoomInfo() {
        
        // 1. 창고(DB)에서 첫 번째 펫(루파) 정보 꺼내오기
        Pet pet = petRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("펫 정보가 없습니다."));

        // 2. 창고(DB)에서 배치된 가구 목록 전부 꺼내오기
        List<RoomFurniture> furnitures = furnitureRepository.findAll();

        // 3. 꺼내온 진짜 펫 데이터를 프론트엔드가 원하는 포장지(DTO)에 담기
        RoomResponseDto.PetDto petDto = RoomResponseDto.PetDto.builder()
                .name(pet.getName())
                .hunger(pet.getHunger())
                .energy(pet.getEnergy())
                .currentAction(pet.getCurrentAction())
                .build();

        // 4. 꺼내온 진짜 가구 데이터를 포장지(DTO)에 담기
        List<RoomResponseDto.FurnitureDto> furnitureDtos = furnitures.stream()
                .map(f -> RoomResponseDto.FurnitureDto.builder()
                        .id(f.getId().intValue())
                        .type(f.getType())
                        .x(f.getX())
                        .y(f.getY())
                        .build())
                .collect(Collectors.toList());

        RoomResponseDto.RoomDto roomDto = RoomResponseDto.RoomDto.builder()
                .furnitureList(furnitureDtos)
                .build();

        // 5. 최종 응답!
        return RoomResponseDto.builder()
                .pet(petDto)
                .room(roomDto)
                .build();
    }
}