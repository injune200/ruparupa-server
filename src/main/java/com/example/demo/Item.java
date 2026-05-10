package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor
public class Item {

    @Id
    private String itemId; // 예: "item_hat_1"

    @Column(nullable = false)
    private int price; // 서버에 저장된 실제 가격

    public Item(String itemId, int price) {
        this.itemId = itemId;
        this.price = price;
    }
}