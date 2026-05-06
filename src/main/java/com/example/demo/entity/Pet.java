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
public class Pet {
    
    @Id // 이 테이블의 고유 ID (기본키)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 1, 2, 3... 자동 증가
    private Long id;

    private String uid; // 유저 식별자 (소셜 로그인 유저와 매핑하기 위함)
    private String name; // 이름 (예: 루파)
    
    // --- 1. 상태 시스템 ---
    private int hunger;      // 배고픔 (내부 계산: 포만감)
    private int energy;      // 피로도 (내부 계산: 컨디션)
    private int cleanliness; // 청결도
    private int happiness;   // 행복도
    
    // --- 2. 외형 정보 (최초 로그인 시 랜덤 부여될 항목들) ---
    private int headSize;
    private int bodySize;
    private int eyeDesign;
    private int noseDesign;
    private int mouthDesign;

    // --- 3. 펫 고유 속성 ---
    private boolean isEgg; // 알 상태 여부 (True/False)
    private String personality; // 성격 유형 (활발 / 차분 / 게으름)
    private String decoration = "None"; // 치장 (기본값 None)
    
    private String currentAction; // 현재 행동 (예: sleeping, eating 등)
}