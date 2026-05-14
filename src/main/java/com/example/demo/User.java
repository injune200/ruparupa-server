package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(unique = true, nullable = false)
    private String uid;

    // 친구 추가용 고유 코드 (예: LUPA5B0RI)
    @Column(unique = true)
    private String friendCode;

    @Column(nullable = false)
    private Long gold = 0L;

    @Column(name = "last_heartbeat_at")
    private LocalDateTime lastHeartbeatAt;

    public User(String nickname, Long kakaoId) {
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.gold = 0L;
        this.uid = "user_" + UUID.randomUUID().toString().substring(0, 8);
        
        // LUPA + 랜덤 문자 5자리로 친구 코드 자동 생성
        String randomStr = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.friendCode = "LUPA" + randomStr; 
        
        this.lastHeartbeatAt = LocalDateTime.now();
    }

    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
        }
    }
}