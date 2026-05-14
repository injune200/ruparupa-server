package com.example.demo.entity;

// 친구 관계 상태를 정리

public enum FriendshipStatus {
    NONE, 
    PENDING_SENT, 
    PENDING_RECEIVED, 
    ACCEPTED, 
    REJECTED, 
    CANCELED, 
    BLOCKED
}