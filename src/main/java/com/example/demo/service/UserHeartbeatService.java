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
                        pet.setSatiety(Math.max(pet.getSatiety() - hungerDecrease, 0));
                        
                        // 자고 있는 상태가 아니라면 비타민도 감소 (자연 감소)
                        if (!pet.isSleep()) {
                            pet.setVitality(Math.max(pet.getVitality() - staminaDecrease, 0));
                        } else {
                            // 만약 자고 있었다면? 기존처럼 비타민 회복 로직 유지
                            int staminaIncrease = (int) (ticks * 2); // 예: 잠잘 땐 10초당 2씩 회복
                            pet.setVitality(Math.min(pet.getVitality() + staminaIncrease, 100));
                            
                            // 비타민이 100이 되면 잠에서 깸 (IDLE 상태를 isSleep = false 로 표현)
                            if (pet.getVitality() >= 100) {
                                pet.setSleep(false);
                            }
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