package com.example.roomlook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlazaRoomResponse {

    private PlazaDetail plaza;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlazaDetail {
        private String plazaId;
        private String plazaCode;
        private String displayPlazaCode;
        private List<PlazaParticipantResponse> participants;
        private List<PlazaChatMessageResponse> messages;
        private int maxParticipants;
        private int roomRevision;
    }
}