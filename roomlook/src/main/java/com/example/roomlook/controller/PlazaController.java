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
    public ResponseEntity<Map<String, Object>> joinRandom(
            @RequestAttribute("currentUid") String currentUid) { 
        
        
        return ResponseEntity.ok(plazaService.joinPlaza(currentUid, "")); 
    }

    // 2. 코드 광장 입장
    @PostMapping("/code/join")
    public ResponseEntity<Map<String, Object>> joinByCode(
            @RequestAttribute("currentUid") String currentUid,   
            @RequestBody Map<String, Object> request) {
        
        String code = request.get("code").toString(); // Body에서는 입장할 방의 code만 추출!

        return ResponseEntity.ok(plazaService.joinPlaza(currentUid, code));
    }

    // 3. 현재 광장 조회
    @GetMapping("/me/active")
    public ResponseEntity<Map<String, Object>> getMyActivePlaza(
            @RequestAttribute("currentUid") String currentUid) { 

        try {
            return ResponseEntity.ok(plazaService.getCurrentPlaza(currentUid));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("plaza", null));
        }
    }
}