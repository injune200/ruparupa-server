package com.example.roomlook.dto;

import com.example.roomlook.entity.Invitation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcceptHomeInvitationResponse {
    private Invitation invitation;
    private HomeSnapshot homeSnapshot;
}