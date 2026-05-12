package com.example.roomlook.controller;

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

    // 1. 랜덤 광장 입장
    @PostMapping("/random/join")
    public ResponseEntity<Map<String, Object>> joinRandom(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String nickname = request.get("nickname").toString();

        // 랜덤 입장이므로 코드는 빈 값으로 전달
        return ResponseEntity.ok(plazaService.joinPlaza(userId, nickname, ""));
    }

    // 2. 코드 광장 입장
    @PostMapping("/code/join")
    public ResponseEntity<Map<String, Object>> joinByCode(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String nickname = request.get("nickname").toString();
        String code = request.get("code").toString(); // Body에 담긴 code 추출

        return ResponseEntity.ok(plazaService.joinPlaza(userId, nickname, code));
    }

    // 3. 현재 광장 조회
    @GetMapping("/me/active")
    public ResponseEntity<Map<String, Object>> getMyActivePlaza(@RequestParam Long userId) {

        try {
            return ResponseEntity.ok(plazaService.getCurrentPlaza(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("plaza", null));
        }
    }
}