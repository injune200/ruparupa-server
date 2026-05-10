package com.example.roomlook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Furniture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;          // 유저 식별자
    private String furnitureId;  // 가구 종류

    private int x;               // 배치된 X 좌표
    private int y;               // 배치된 Y 좌표
    private int width;           // 가구의 가로 칸수
    private int height;          // 가구의 세로 칸수
    //실제로 내 방에 놓인 물건
}
