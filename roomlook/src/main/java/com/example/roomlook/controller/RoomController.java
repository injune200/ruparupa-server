package com.example.roomlook.controller;

import com.example.roomlook.entity.Furniture;
import com.example.roomlook.service.FurnitureService;
import com.example.roomlook.repository.FurnitureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final FurnitureService furnitureService;
    private final FurnitureRepository furnitureRepository;

    // 가구 배치 저장 API
    @PostMapping("/save/{uid}")
    public String saveRoom(@PathVariable String uid, @RequestBody List<Furniture> layout) {
        try {
            furnitureService.saveRoomLayout(uid, layout);
            return "방 배치가 성공적으로 저장되었습니다!";
        } catch (Exception e) {
            return "저장 실패: " + e.getMessage();
        }
    }

    // 배치된 가구 불러오기 API (광장 대화 및 초대 기능 연동)
    @GetMapping("/load/{uid}")
    public List<Furniture> loadRoom(@PathVariable String uid) {
        return furnitureRepository.findByUid(uid);
    }
}