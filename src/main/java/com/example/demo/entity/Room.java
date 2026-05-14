package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "rooms")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    private String roomId; // PK: uid-랜덤숫자 10자리

    private String ownerUserId; // 방 주인 uID

    @Builder.Default
    private String sceneId = "main_room";

    @Builder.Default
    private int layoutRevision = 0;

    @Builder.Default
    private String wallAssetKey = "room/walls/main_wall";

    @Builder.Default
    private String floorAssetKey = "room/floors/main_floor";

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Room ID 생성 헬퍼 메서드
    public void generateRoomId(String userUid) {
        StringBuilder sb = new StringBuilder(userUid);
        sb.append("-");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        this.roomId = sb.toString();
    }
}