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
    
    private int x; 
    private int y;
    
    private int direction; // 방향 (예: 0, 90, 180, 270 등 회전 각도 또는 0,1,2,3 방향키)
    private String status = "unused"; // 사용 여부 (used / unused)

    // 어떤 유저(또는 방)의 가구인지 연결하기 위한 방 ID
    private Long roomId; 
}