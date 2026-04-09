package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoomFurniture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // 가구 종류 (예: bed, sofa, bowl)
    
    // 기획서 [2-1-2. 좌표 체계] 논리 좌표 기준
    private int x; 
    private int y;
    
    // 어떤 유저(또는 방)의 가구인지 연결하기 위한 방 ID (일단 단순화)
    private Long roomId; 
}