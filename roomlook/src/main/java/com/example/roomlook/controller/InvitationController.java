package com.example.roomlook.controller;

import com.example.roomlook.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend-home-invitations") // API 계약서 주소
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    // 초대 보내기
    @PostMapping
    public String invite(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam Long roomId) {
        return invitationService.sendInvitation(senderId, receiverId, roomId);
    }

    // 초대 수락하기
    @PostMapping("/{id}/accept")
    public Object accept(@PathVariable Long id) {
        return invitationService.acceptInvitation(id);
    }
}