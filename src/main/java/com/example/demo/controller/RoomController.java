package com.example.demo.controller;

import com.example.demo.dto.RoomResponseDto;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFurniture;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomFurnitureRepository;
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final PetRepository petRepository;
    private final RoomRepository roomRepository;
    private final RoomFurnitureRepository roomFurnitureRepository;

    @GetMapping("/room")
    public RoomResponseDto getRoomInfo(@RequestParam(name = "petId", defaultValue = "1") Long petId) {
        // 1. 펫 정보 가져오기
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        // 2. 펫의 방(Room) 정보 가져오기 (없으면 에러 처리)
        Room room = roomRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 펫의 방이 존재하지 않습니다."));

        // 3. 방에 배치된 가구 리스트 가져오기
        List<RoomFurniture> furnitureList = roomFurnitureRepository.findByRoomId(room.getId());

        // 4. 가구 엔티티들을 DTO로 변환
        List<RoomResponseDto.FurnitureDto> furnitureDtos = furnitureList.stream()
                .map(f -> RoomResponseDto.FurnitureDto.builder()
                        .id(Math.toIntExact(f.getId()))
                        .type(f.getType())
                        .x(f.getX())
                        .y(f.getY())
                        .direction(f.getDirection())
                        .status(f.getStatus())
                        .build())
                .collect(Collectors.toList());

        // 5. 최종 응답 DTO 조립
        RoomResponseDto.PetDto petDto = RoomResponseDto.PetDto.builder()
                .name(pet.getName())
                .hunger(pet.getHunger())
                .energy(pet.getEnergy())
                .currentAction(pet.getCurrentAction())
                .build();

        RoomResponseDto.RoomDto roomDto = RoomResponseDto.RoomDto.builder()
                .wallType(room.getWallType())           // DB에 저장된 벽지 세팅
                .floorTileType(room.getFloorTileType()) // DB에 저장된 타일 세팅
                .furnitureList(furnitureDtos)
                .build();

        return RoomResponseDto.builder()
                .pet(petDto)
                .room(roomDto)
                .build();
    }
}