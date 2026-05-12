package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.dto.UserHeartbeatDto;
import com.example.demo.entity.Pet; 
import com.example.demo.repository.PetRepository; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserHeartbeatService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private static final int TIMEOUT_SECONDS = 180; // 3분 이상 비접속 시 오프라인으로 간주
    private static final int TICK_SECONDS = 10;     // 1틱 기준: 10초

    @Transactional
    public UserHeartbeatDto.HeartbeatResponse processHeartbeat(String requestUid) {
        User user = userRepository.findByUid(requestUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBeat = user.getLastHeartbeatAt();
        
        boolean isReconnected = false;
        long offlineSeconds = 0;

        if (lastBeat != null) {
            offlineSeconds = Duration.between(lastBeat, now).getSeconds();

            // 1. 마지막 접속 후 3분(TIMEOUT_SECONDS) 이상 지났다면 오프라인 정산 실행
            if (offlineSeconds >= TIMEOUT_SECONDS) {
                isReconnected = true;
                
                // 2. 총 몇 틱(10초 단위)이 지났는지 계산
                long ticks = offlineSeconds / TICK_SECONDS;

                if (ticks > 0) {
                    petRepository.findByUserId(user.getId()).ifPresent(pet -> {
                        // 3. 기획안 반영: 1틱당 hunger -2, stamina -1
                        int hungerDecrease = (int) (ticks * 2);
                        int staminaDecrease = (int) (ticks * 1);

                        // 4. 수치 감소 (0 미만으로 떨어지지 않게 처리)
                        pet.setHunger(Math.max(pet.getHunger() - hungerDecrease, 0));
                        
                        // 자고 있는 상태가 아니라면 스태미나도 감소 (자연 감소)
                        if (!"SLEEPING".equals(pet.getCurrentAction())) {
                            pet.setStamina(Math.max(pet.getStamina() - staminaDecrease, 0));
                        } else {
                            // 만약 자고 있었다면? 기존처럼 스태미나 회복 로직을 유지하거나 기획에 맞춰 조정 가능
                            int staminaIncrease = (int) (ticks * 2); // 예: 잠잘 땐 10초당 2씩 회복
                            pet.setStamina(Math.min(pet.getStamina() + staminaIncrease, 100));
                            if (pet.getStamina() >= 100) pet.setCurrentAction("IDLE");
                        }
                        
                        petRepository.save(pet);
                    });
                }
            }
        }

        user.setLastHeartbeatAt(now);

        return UserHeartbeatDto.HeartbeatResponse.builder()
                .status("success")
                .lastHeartbeatAt(now)
                .isReconnected(isReconnected)
                .offlineSeconds(offlineSeconds)
                .build();
    }

    @Transactional(readOnly = true)
    public UserHeartbeatDto.StatusResponse getUserStatus(String requestUid) {
        User user = userRepository.findByUid(requestUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        LocalDateTime lastBeat = user.getLastHeartbeatAt();
        LocalDateTime now = LocalDateTime.now();
        boolean isOnline = false;

        if (lastBeat != null) {
            long diff = Duration.between(lastBeat, now).getSeconds();
            isOnline = diff < TIMEOUT_SECONDS; 
        }

        return UserHeartbeatDto.StatusResponse.builder()
                .status("success")
                .userId(requestUid)
                .isOnline(isOnline)
                .lastSeenAt(lastBeat)
                .build();
    }
}