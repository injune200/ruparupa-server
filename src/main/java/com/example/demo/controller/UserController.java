package com.example.demo.controller;

import com.example.demo.dto.FriendDto;
import com.example.demo.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final FriendService friendService;

    // 내 친구 코드 조회 API (경로: GET /users/me/friend-code)
    @GetMapping("/me/friend-code")
    public ResponseEntity<?> getMyFriendCode(@RequestAttribute("currentUid") String currentUid) { 
        try {
            FriendDto.MyFriendCodeResponse response = friendService.getMyFriendCode(currentUid);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}