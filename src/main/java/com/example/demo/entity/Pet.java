package com.example.demo.entity;

import com.example.demo.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 펫은 유저와 1:1 관계 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name; // 펫 이름
    
    @Column(nullable = false)
    private String personality; // 성격: ACTIVE, CALM, LAZY

    // 기획 수치 반영 (포만감, 활력 모두 100으로 시작)
    @Builder.Default
    @Column(nullable = false)
    private int hunger = 100;

    @Builder.Default
    @Column(nullable = false)
    private int stamina = 100; 

    // 프론트엔드 명세 반영: 초기 상태는 IDLE
    @Builder.Default
    @Column(nullable = false)
    private String currentAction = "IDLE";
}