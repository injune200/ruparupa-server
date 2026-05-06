package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrencyService {

    private final UserRepository userRepository;

    public CurrencyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CurrencyService.java
    @Transactional
    public Long gainGoldWithVerification(String nickname, int amount, Long clientTotal) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 검증 로직: 서버 DB 잔액 + 획득량 == 클라이언트가 보낸 최종 금액
        Long serverCalculatedTotal = user.getGold() + amount;

        if (!serverCalculatedTotal.equals(clientTotal)) {
            // 일치하지 않으면 조작된 데이터로 간주하고 예외 발생
            throw new RuntimeException("데이터 부정합 감지: 서버 계산과 클라이언트 데이터가 일치하지 않습니다.");
        }

        // 검증 통과 시 재화 추가 및 저장
        user.addGold(amount);
        userRepository.save(user);
    
        return user.getGold();
    }
}