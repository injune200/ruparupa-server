package com.example.roomlook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private Long roomId;

    private String status; // PENDING, ACCEPTED, REJECTED 등

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;


    private String message;           // 우리 집 구경 올래? 메시지 저장 칸
    private LocalDateTime respondedAt; // 수락이나 거절을 누른 시간 기록 칸
}