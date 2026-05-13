package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RoomLayoutResponseDto {
    private LayoutData roomLayout;

    @Getter
    @Builder
    public static class LayoutData {
        private String roomId;
        private String ownerUserId;
        private String sceneId;
        private int layoutRevision;
        private String wallAssetKey;
        private String floorAssetKey;
        private List<FurnitureData> placedItems;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class FurnitureData {
        private String id; // 프론트 요구사항에 맞춰 가구 ID도 String으로 처리 가능
        private String type;
        private int x;
        private int y;
        private int direction;
        private String status;
    }
}