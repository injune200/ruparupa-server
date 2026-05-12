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
import org.springframework.web.bind.annotation.RequestAttribute; 
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
    public RoomResponseDto getRoomInfo(
            @RequestAttribute("currentUid") String currentUid, 
            @RequestParam(name = "petId", defaultValue = "1") Long petId) {
            
        // 1. 펫 정보 가져오기
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("펫이 존재하지 않습니다."));

        // 소유권 검증 로직 추가: 내 펫(방)이 아니면 예외 발생
        if (!pet.getUser().getUid().equals(currentUid)) {
            throw new IllegalArgumentException("본인의 방 정보만 조회할 수 있습니다.");
        }

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
                .stamina(pet.getStamina()) 
                .currentAction(pet.getCurrentAction())
                .build();

        RoomResponseDto.RoomDto roomDto = RoomResponseDto.RoomDto.builder()
                .wallType(room.getWallType())
                .floorTileType(room.getFloorTileType())
                .furnitureList(furnitureDtos)
                .build();

        return RoomResponseDto.builder()
                .pet(petDto)
                .room(roomDto)
                .build();
    }
}