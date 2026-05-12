package com.example.roomlook.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PlazaRoomResponse {
    private String plazaId;
    private String plazaCode;
    private String displayPlazaCode;
    private List<PlazaParticipantResponse> participants;
    private List<Object> messages;
    private List<Object> interactions; // 계약서 명시 필드 추가
    private Integer maxParticipants;   // 기본값 4
    private Integer roomRevision;
    private Map<String, Long> serverTime;
    private Long joinedAtMillis;
    private Boolean isServerAuthoritative; // 항상 true 명시 필드 추가
}