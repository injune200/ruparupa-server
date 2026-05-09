package com.example.demo.entity;

// 요청을 수락했을 때 만들어지는 친구 목록 데이터

import com.example.demo.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor // Builder를 쓰기 위해 필수
@Builder            // ⭐ 추가됨!
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
    @Builder.Default // 빌더를 쓸 때 기본값이 무시되지 않도록 설정
    private FriendshipStatus status = FriendshipStatus.ACCEPTED;

    private LocalDateTime friendsSince;

    @PrePersist
    protected void onCreate() {
        this.friendsSince = LocalDateTime.now();
    }
}