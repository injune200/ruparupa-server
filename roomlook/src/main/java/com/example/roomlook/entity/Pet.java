package com.example.roomlook.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerUserId; // 조종하는 주인의 ID
    private String name;      // 펫의 이름
    private String characterAssetKey; // 펫 종류


    private Float headSizeScale = 1.0f;
    private Float bodySizeScale = 1.0f;
    private Float eyeSizeScale = 1.0f;
    private Float noseSizeScale = 1.0f;
    private Float mouthSizeScale = 1.0f;
}
