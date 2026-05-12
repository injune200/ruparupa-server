package com.example.demo.dto;

import com.example.demo.entity.FriendRequestStatus;
import com.example.demo.entity.FriendshipStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class FriendDto {

    // ==========================================
    // 1. 공통 에러 응답
    // ==========================================
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ErrorResponse {
        private String code;
        private String message;
    }

    // ==========================================
    // 2. 공통 데이터 모델 (MD 계약서 기준)
    // ==========================================
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendUser {
        private String userId;
        private String nickname;
        private String friendCode;
        private String displayFriendCode;
        private String avatarAssetKey;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendRequest {
        private String id;
        private FriendUser fromUser;
        private FriendUser toUser;
        private FriendRequestStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime respondedAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendSummary {
        private String friendshipId;
        private FriendUser user;
        private FriendshipStatus status;
        private LocalDateTime friendsSince;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendMessage {
        private String id;
        private String friendUserId;
        private String senderUserId;
        private String text; // 계약서에 맞춰 content -> text 로 변경
        private LocalDateTime sentAt;
    }

    // ==========================================
    // 3. API 요청 (Request) DTO
    // ==========================================
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SendRequest {
        private String friendCode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MessageRequest {
        private String text;
    }

    // ==========================================
    // 4. API 응답 (Response) DTO
    // ==========================================

    // 친구 신청, 거절, 취소 응답
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SingleRequestResponse {
        private FriendRequest request;
    }

    // 친구 수락 응답 (request와 friendship 동시 반환)
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AcceptRequestResponse {
        private FriendRequest request;
        private FriendSummary friendship;
    }

    // 내 친구 코드 조회
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MyFriendCodeResponse {
        private String friendCode;
        private String displayFriendCode;
    }

    // 친구 코드로 유저 검색
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendUserLookupResponse {
        private FriendUser user;
        private FriendshipStatus relationshipStatus;
    }

    // 보낸/받은 요청 목록 (이제 API가 분리되므로 각각 requests 배열만 가짐)
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendRequestListResponse {
        private List<FriendRequest> requests;
    }

    // 친구 목록 조회
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendListResponse {
        private List<FriendSummary> friends;
    }

    // 메시지 1개 전송 응답
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SingleMessageResponse {
        private FriendMessage message;
    }

    // 메시지 목록 조회 페이징 응답
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FriendMessagesResponse {
        private List<FriendMessage> messages;
        private String nextCursor;
    }
}