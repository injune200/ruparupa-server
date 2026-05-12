package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import java.util.Random;

@Entity
@Table(name = "inventories")
@Getter @Setter
@NoArgsConstructor
public class Inventory {

    @Id
    private String instanceId; // uuid + 랜덤 10자리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String itemId;
    private String itemName;
    private int count;

    public Inventory(User user, Item item, int count) {
        this.user = user;
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.count = count;
        this.instanceId = generateInstanceId();
    }

    private String generateInstanceId() {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return uuid + sb.toString();
    }
}