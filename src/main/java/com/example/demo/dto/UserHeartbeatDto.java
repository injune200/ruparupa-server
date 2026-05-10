package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

public class UserHeartbeatDto {

    @Getter @Setter
    public static class Request {
        private String userId; // 프론트에서 보내는 uid 
    }

    @Getter @Builder
    public static class HeartbeatResponse {
        private String status;
        private LocalDateTime lastHeartbeatAt;
        private boolean isReconnected; 
        private long offlineSeconds;   
    }

    @Getter @Builder
    public static class StatusResponse {
        private String status;
        private String userId;
        private boolean isOnline;
        private LocalDateTime lastSeenAt;
    }
}