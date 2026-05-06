package com.example.roomlook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Itemmaster {
    @Id
    private String furnitureId;  // 가구 종류 ID (예: 'bed_01', 'chair_02')

    private String name;         // 가구 이름
    private int width;           // 가구의 기본 가로 칸수
    private int height;          // 가구의 기본 세로 칸수
    //도감 모든 가구의 원본정보
}
