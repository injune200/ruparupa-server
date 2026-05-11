package com.example.roomlook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeSnapshot {

    private RoomSnapshot room; // 첫 번째

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomSnapshot {
        private Object placedItems; // 실제 가구들이 담길 곳
    }
}