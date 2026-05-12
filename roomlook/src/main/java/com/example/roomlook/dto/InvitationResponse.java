package com.example.roomlook.dto;

import com.example.roomlook.entity.Invitation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvitationResponse {
    private Invitation invitation;
}