package com.example.demo.entity;

import com.example.demo.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_messages")
@Getter @Setter 
@NoArgsConstructor 
@AllArgsConstructor // 빌더 사용을 위해 추가
@Builder            // 빌더 패턴 사용을 위해 추가
public class FriendMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지를 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // 메시지를 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    // 메시지 내용 (계약서 규격에 따라 120자로 제한)
    @Column(nullable = false, length = 120) 
    private String content;

    // 보낸 시간
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}