package com.example.roomlook.dto;

import com.example.roomlook.dto.PlazaMovementCommand;
import com.example.roomlook.dto.PlazaPosition;
import lombok.Data;

@Data
public class PlazaParticipantResponse {
    private String userId;
    private String nickname;
    private PlazaPetSnapshot pet;
    private Long joinedAtMillis;
    private PlazaPosition position;
    private PlazaMovementCommand movement; // 이동 중이 아니면 null
    private Long positionUpdatedAtMillis;
}
//참가자 응답