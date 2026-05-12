package com.example.roomlook.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlazaPetSnapshot {
    private String petId;
    private String ownerUserId;
    private String name;
    private String characterAssetKey;
    private Object appearance; // 상세 외형
    private Object status;     // 배고픔, 활력
    private String personality;
    private List<String> equippedItemIds;
}