package com.example.demo.repository;
// 받은 메시지 목록을 최신순으로 가져올 창고

import com.example.demo.User;
import com.example.demo.entity.FriendMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendMessageRepository extends JpaRepository<FriendMessage, Long> {
    
    // 내가 받은 메시지들을 최신순(내림차순)으로 가져오기
    List<FriendMessage> findByToUserOrderByCreatedAtDesc(User toUser);
}