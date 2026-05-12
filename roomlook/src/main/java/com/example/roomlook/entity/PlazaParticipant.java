package com.example.roomlook.entity; // 반드시 이 주소여야 합니다!

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlazaParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;

    private Long userId;
    private String nickname;

    // 펫 관련 정보
    private Long petId;

    // 입장 시간 (계약서 필수 항목)
    private Long joinedAtMillis;

    // 위치 정보
    private Float positionX;
    private Float positionY;

    // 이동 관련
    private Float targetX;
    private Float targetY;
    private Long moveStartedAtMillis;
    private Long moveDurationMillis;

    private Long lastUpdatedAtMillis;
}