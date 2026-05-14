package com.example.demo.entity;

// 친구 요청 상태를 관리

public enum FriendRequestStatus {
    PENDING,  // 대기 중
    ACCEPTED, // 수락됨
    REJECTED, // 거절됨
    CANCELED  // 취소됨
}