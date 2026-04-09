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

    private String name; // 이름 (예: 루파)
    
    // 기획서 [1-3. 상태 시스템] 기준 수치들 (0~100)
    private int hunger;      // 배고픔 (내부 계산: 포만감)
    private int energy;      // 피로도 (내부 계산: 컨디션)
    private int cleanliness; // 청결도
    private int happiness;   // 행복도
    
    private String currentAction; // 현재 행동 (예: sleeping, eating 등)
}