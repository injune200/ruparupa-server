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
    private int itemId;

    @Column(nullable = false)
    private String itemName; // 아이템 이름 필드 추가

    @Column(nullable = false)
    private int price; // 서버에 저장된 실제 가격

    public Item(int itemId, String itemName,int price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
    }
}