package com.example.demo.dto;

// 프론트엔드와 통신할 때 사용할 데이터 규격. 에러 응답과 성공 응답을 모두 포함

import com.example.demo.entity.FriendRequestStatus;
import com.example.demo.entity.FriendshipStatus;
import lombok.*;

import java.time.LocalDateTime;

public class FriendDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String friendCode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String fromUserId;
        private String fromNickname;
        private String toUserId;
        private String toNickname;
        private FriendRequestStatus status;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ErrorResponse {
        private String code;
        private String message;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendUserDto {
        private String userId;
        private String nickname;
        private String friendCode;
        private String displayFriendCode; // 화면 표시용 (LUPA-ABCDE)
        private String avatarAssetKey;    // 프로필 이미지 (MVP에선 null)
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendSummary {
        private String friendshipId;
        private FriendUserDto user;
        private FriendshipStatus status;
        private LocalDateTime friendsSince;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendListResponse {
        private java.util.List<FriendSummary> friends;
    }
}