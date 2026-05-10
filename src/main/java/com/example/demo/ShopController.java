package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * 상점 아이템 가격 검증 API
     */
    @PostMapping("/items")
    public ResponseEntity<?> verifyShopItems(@RequestBody ShopRequest shopRequest) {
        // 가격이 불일치하는 아이템 리스트를 가져옴
        List<ItemResponse> mismatchedItems = shopService.getMismatchedItems(shopRequest.getItems());

        if (mismatchedItems.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "모든 아이템 가격이 일치합니다."));
        } else {
            return ResponseEntity.status(400).body(Map.of(
                "status", "fail",
                "message", "일부 아이템의 가격이 일치하지 않습니다.",
                "mismatchedItems", mismatchedItems
            ));
        }
    }
}

class ShopRequest {
    private List<ItemRequest> items;
    public List<ItemRequest> getItems() { return items; }
    public void setItems(List<ItemRequest> items) { this.items = items; }
}

class ItemRequest {
    private String itemId;
    private int price;
    public String getItemId() { return itemId; }
    public int getPrice() { return price; }
}

class ItemResponse {
    private String itemId;
    private int serverPrice; // 서버에 기록된 실제 가격

    public ItemResponse(String itemId, int serverPrice) {
        this.itemId = itemId;
        this.serverPrice = serverPrice;
    }
    public String getItemId() { return itemId; }
    public int getServerPrice() { return serverPrice; }
}