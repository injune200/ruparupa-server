package com.example.demo.repository;

import com.example.demo.User;
import com.example.demo.entity.FriendRequest;
import com.example.demo.entity.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // A가 B에게 특정 상태(예: PENDING)인 요청을 이미 보냈는지 확인하는 용도
    boolean existsByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendRequestStatus status);
}

