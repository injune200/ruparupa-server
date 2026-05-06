package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long kakaoId; // 카카오에서 주는 고유 번호 (식별용)
    
    @Column(nullable = false)
    private Long gold = 0L; // 기본값 0으로 설정

    public User(String nickname, Long kakaoId) {
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.gold = 0L;
    }

    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
        }
    }
}