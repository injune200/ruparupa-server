package com.example.demo.controller;

import com.example.demo.dto.UserHeartbeatDto;
import com.example.demo.service.UserHeartbeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHeartbeatController {

    private final UserHeartbeatService heartbeatService;

    @PostMapping("/heartbeat")
    public ResponseEntity<UserHeartbeatDto.HeartbeatResponse> heartbeat(
            @RequestAttribute("currentUid") String currentUid, // ⭐ 토큰에서 뽑은 진짜 UID
            @RequestBody(required = false) UserHeartbeatDto.Request request) { 
        
        return ResponseEntity.ok(heartbeatService.processHeartbeat(currentUid));
    }

    @GetMapping("/status")
    public ResponseEntity<UserHeartbeatDto.StatusResponse> getStatus(
            @RequestAttribute("currentUid") String currentUid) { 
            
        return ResponseEntity.ok(heartbeatService.getUserStatus(currentUid));
    }
}