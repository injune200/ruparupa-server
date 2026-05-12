package com.example.roomlook.dto;

import lombok.Data;

@Data
public class InvitationRequest {
    private Long friendUserId; // 누구를 초대할지
    private String message;    // 초대 메시지
}
