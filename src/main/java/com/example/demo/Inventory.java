package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Random;

@Entity
@Table(name = "inventories")
@Getter @Setter
@NoArgsConstructor
public class Inventory {

    @Id
    private String instanceId; // 유저 uid + "-" + 랜덤 10자리

    @ManyToOne(fetch = FetchType.LAZY)
    // referencedColumnName을 사용하여 User 엔티티의 uid 필드를 참조하도록 설정
    @JoinColumn(name = "uid", referencedColumnName = "uid") 
    private User user;

    private int itemId;
    private String itemName;
    private int count;

    public Inventory(User user, Item item, int count) {
        this.user = user;
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.count = count;
        // 유저의 uid를 기반으로 instanceId 생성
        this.instanceId = generateInstanceId(user.getUid());
    }

    private String generateInstanceId(String userUid) {
        StringBuilder sb = new StringBuilder(userUid);
        sb.append("-"); // uid와 숫자 사이에 하이픈 추가
        
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}