package com.example.roomlook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlazaChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plazaId;
    private Long senderUserId;
    private String senderNickname;

    @Column(length = 120) // 최대 120자 제한
    private String text;

    private Long sentAtMillis;
}
//채팅기록