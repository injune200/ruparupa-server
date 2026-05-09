package com.example.demo.entity;

// 요청을 수락했을 때 만들어지는 진짜 친구 목록 데이터

import com.example.demo.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기준 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 친구인 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.ACCEPTED;

    private LocalDateTime friendsSince;

    @PrePersist
    protected void onCreate() {
        this.friendsSince = LocalDateTime.now();
    }
}