package com.example.roomlook.controller;

import com.example.roomlook.dto.*;
import com.example.roomlook.service.PlazaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;

    /**
     * 1. 랜덤 광장 입장
     */
    @PostMapping("/random/join")
    public ResponseEntity<PlazaRoomResponse> joinRandomPlaza(
            @RequestAttribute("currentUid") Long userId,
            @RequestAttribute("currentNickname") String nickname) {
        // 랜덤 입장은 코드가 없으므로 null 전달
        return ResponseEntity.ok(plazaService.joinPlaza(userId, nickname, null));
    }

    /**
     * 2. 광장 코드 입장
     */
    @PostMapping("/code/join")
    public ResponseEntity<PlazaRoomResponse> joinCodePlaza(
            @RequestAttribute("currentUid") Long userId,
            @RequestAttribute("currentNickname") String nickname,
            @RequestBody Map<String, String> request) {
        String code = request.get("code");
        return ResponseEntity.ok(plazaService.joinPlaza(userId, nickname, code));
    }

    /**
     * 3. 현재 광장 조회
     */
    @GetMapping("/me/active")
    public ResponseEntity<PlazaRoomResponse> getMyActivePlaza(
            @RequestAttribute("currentUid") Long userId) {
        // Service에 해당 메서드가 없다면 스냅샷 로직을 활용해 구현 필요
        return ResponseEntity.ok(plazaService.getCurrentPlazaMvp(userId));
    }

    /**
     * 4. 광장 스냅샷 조회
     */
    @GetMapping("/{plazaId}")
    public ResponseEntity<PlazaRoomResponse> getPlazaSnapshot(
            @RequestAttribute("currentUid") Long userId,
            @PathVariable String plazaId) {
        return ResponseEntity.ok(plazaService.getPlazaSnapshot(plazaId, userId));
    }

    /**
     * 5. 광장 퇴장
     */
    @PostMapping("/{plazaId}/leave")
    public ResponseEntity<Void> leavePlaza(
            @RequestAttribute("currentUid") Long userId,
            @PathVariable String plazaId) {
        plazaService.leavePlaza(plazaId, userId);
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }

    /**
     * 6. 광장 채팅 전송
     */
    @PostMapping("/{plazaId}/messages")
    public ResponseEntity<PlazaChatMessageResponse> sendPlazaMessage(
            @RequestAttribute("currentUid") Long userId,
            @PathVariable String plazaId,
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        return ResponseEntity.ok(plazaService.sendPlazaMessage(plazaId, userId, text));
    }
}//.