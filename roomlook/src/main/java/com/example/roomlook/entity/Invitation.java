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

    private Long senderId;    // 초대를 보낸 사람 ID
    private Long receiverId;  // 초대 받은 친구 ID
    private Long roomId;      // 초대할 방의 ID

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; // 만료 시간
}