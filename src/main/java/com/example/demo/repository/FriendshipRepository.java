package com.example.demo.repository;

import com.example.demo.User;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.FriendshipStatus;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 두 유저가 이미 친구인지 확인하는 용도
    boolean existsByUserAndFriend(User user, User friend);

    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status); // 특정 유저의 수락된 친구 목록을 가져오는 메서드
}