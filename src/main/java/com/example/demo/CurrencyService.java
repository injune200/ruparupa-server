package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrencyService {

    private final UserRepository userRepository;

    public CurrencyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 현재 잔액 조회 메서드 추가 (실패 응답용)
    public Long getCurrentGold(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(User::getGold)
                .orElse(0L);
    }

    @Transactional
    public Long gainGoldWithVerification(String nickname, int amount, Long clientTotal) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Long serverCalculatedTotal = user.getGold() + amount;

        if (!serverCalculatedTotal.equals(clientTotal)) {
            // 단순히 예외만 던지지 않고, 필요하다면 커스텀 예외를 만들어 잔액을 담을 수 있습니다.
            throw new RuntimeException("데이터 부정합 감지");
        }

        user.addGold(amount);
        userRepository.save(user);
    
        return user.getGold();
    }
}