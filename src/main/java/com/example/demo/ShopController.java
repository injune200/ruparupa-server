package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;
    private final JwtUtil jwtUtil;

    public ShopController(ShopService shopService, JwtUtil jwtUtil) {
        this.shopService = shopService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 상점 구매 및 인벤토리 동기화 API
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItem(@RequestBody PurchaseRequest purchaseRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("status", "fail", "message", "인증이 필요합니다."));
        }

        try {
            String token = authHeader.substring(7);
            String nickname = jwtUtil.extractNickname(token);

            PurchaseResponse response = shopService.purchaseItem(nickname, purchaseRequest);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("status", "fail", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "fail", "message", "서버 오류 발생"));
        }
    }

    /**
     * 상점 아이템 가격 검증 API
     */
    @PostMapping("/items")
    public ResponseEntity<?> verifyShopItems(@RequestBody ShopRequest shopRequest) {
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

/**
 * 아이템 구매 요청 DTO
 */
class PurchaseRequest {
    private int itemId; // String -> int로 변경
    private int amount;
    private int price;
    private Long balance;

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
}

/**
 * 구매 성공 응답 DTO
 */
class PurchaseResponse {
    private String status;
    private int purchasedItemId; // String -> int로 변경
    private Long balance;
    private List<InventoryResponse> inventory;

    public PurchaseResponse(String status, int purchasedItemId, Long balance, List<InventoryResponse> inventory) {
        this.status = status;
        this.purchasedItemId = purchasedItemId;
        this.balance = balance;
        this.inventory = inventory;
    }

    public String getStatus() { return status; }
    public int getPurchasedItemId() { return purchasedItemId; }
    public Long getBalance() { return balance; }
    public List<InventoryResponse> getInventory() { return inventory; }
}

/**
 * 인벤토리 아이템 정보 DTO
 */
class InventoryResponse {
    private String instanceId;
    private int id; 
    private String itemName;
    private int count;

    public InventoryResponse(String instanceId, int id, String itemName, int count) {
        this.instanceId = instanceId;
        this.id = id;
        this.itemName = itemName;
        this.count = count;
    }

    public String getInstanceId() { return instanceId; }
    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public int getCount() { return count; }
}

/**
 * 상점 가격 검증 관련 DTO들
 */
class ShopRequest {
    private List<ItemRequest> items;
    public List<ItemRequest> getItems() { return items; }
    public void setItems(List<ItemRequest> items) { this.items = items; }
}

class ItemRequest {
    private int itemId; // String -> int로 변경
    private String itemName;
    private int price;
    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getPrice() { return price; }
}

class ItemResponse {
    private int itemId; // String -> int로 변경
    private String itemName;
    private int serverPrice;

    public ItemResponse(int itemId, String itemName, int serverPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.serverPrice = serverPrice;
    }
    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getServerPrice() { return serverPrice; }
}