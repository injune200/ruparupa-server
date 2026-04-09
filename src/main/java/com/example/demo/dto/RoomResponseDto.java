package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class RoomResponseDto {
    private PetDto pet;
    private RoomDto room;

    @Getter
    @Builder
    public static class PetDto {
        private String name;
        private int hunger;
        private int energy;
        private String currentAction;
    }

    @Getter
    @Builder
    public static class RoomDto {
        private List<FurnitureDto> furnitureList;
    }

    @Getter
    @Builder
    public static class FurnitureDto {
        private int id;
        private String type;
        private int x;
        private int y;
    }
}