package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.dto.UserHeartbeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserHeartbeatService {

    private final UserRepository userRepository;
    private static final int TIMEOUT_SECONDS = 180; // 3분

    @Transactional
    public UserHeartbeatDto.HeartbeatResponse processHeartbeat(String requestUid) {
        User user = userRepository.findByUid(requestUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBeat = user.getLastHeartbeatAt();
        
        boolean isReconnected = false;
        long offlineSeconds = 0;

        if (lastBeat != null) {
            long diff = Duration.between(lastBeat, now).getSeconds();
            if (diff >= TIMEOUT_SECONDS) {
                isReconnected = true;
                offlineSeconds = diff;
                //TODO: 3분 이상 미접속 시 에너지 감소 등 오프라인 정산 처리
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