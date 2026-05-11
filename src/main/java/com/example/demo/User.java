package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID; // 추가

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;
    
    // 프론트와 통신할 때 사용할 안전한 식별자 uid
    @Column(unique = true, nullable = false)
    private String uid; 

    @Column(nullable = false)
    private Long gold = 0L;

    @Column(name = "last_heartbeat_at")
    private LocalDateTime lastHeartbeatAt;

    public User(String nickname, Long kakaoId) {
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.gold = 0L;
        // 생성 시 "user_a1b2c3d4" 형태로 자동 발급
        this.uid = "user_" + UUID.randomUUID().toString().substring(0, 8); 
        this.lastHeartbeatAt = LocalDateTime.now();
    }

    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
        }
    }
}