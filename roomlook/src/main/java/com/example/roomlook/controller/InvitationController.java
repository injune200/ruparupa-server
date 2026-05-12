package com.example.roomlook.controller;

import com.example.roomlook.dto.AcceptHomeInvitationResponse;
import com.example.roomlook.dto.InvitationRequest;
import com.example.roomlook.dto.InvitationResponse;
import com.example.roomlook.entity.Invitation;
import com.example.roomlook.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends/home-invitations") // 민영님 약속 주소
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    // 1. 초대 보내기
    @PostMapping
    public InvitationResponse sendInvitation(@RequestBody InvitationRequest req) {
        // 실제로는 인증 세션에서 senderId를 가져오겠지만, 일단 1L로 가정, 요청 되는지 확인하기위한 것
        Invitation invitation = invitationService.sendInvitation(1L, req.getFriendUserId(), 1L, req.getMessage());
        return new InvitationResponse(invitation);
    }

    // 2. 초대 수락
    @PostMapping("/{id}/accept")
    public AcceptHomeInvitationResponse acceptInvitation(@PathVariable Long id) {
        return invitationService.acceptInvitation(id);
    }

    // 3. 초대 거절
    @PostMapping("/{id}/reject")
    public InvitationResponse rejectInvitation(@PathVariable Long id) {
        Invitation invitation = invitationService.rejectInvitation(id);
        return new InvitationResponse(invitation);
    }
}