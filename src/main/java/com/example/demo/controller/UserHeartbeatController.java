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
            @RequestBody UserHeartbeatDto.Request request) {
        return ResponseEntity.ok(heartbeatService.processHeartbeat(request.getUserId()));
    }

    @GetMapping("/status")
    public ResponseEntity<UserHeartbeatDto.StatusResponse> getStatus(
            @RequestParam("userId") String userId) {
        return ResponseEntity.ok(heartbeatService.getUserStatus(userId));
    }
}