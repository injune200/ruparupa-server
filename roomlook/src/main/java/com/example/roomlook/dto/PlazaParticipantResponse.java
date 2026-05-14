package com.example.roomlook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlazaParticipantResponse {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private PlazaPetSnapshotResponse pet;
    private PlazaPositionResponse position;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlazaPetSnapshotResponse {
        private String characterAssetKey; // 펫 종류
        private double appearance;        // 펫 크기 배율
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlazaPositionResponse {
        private double x; // 0.0 ~ 1.0 상대 좌표
        private double y; // 0.0 ~ 1.0 상대 좌표
    }
}//.