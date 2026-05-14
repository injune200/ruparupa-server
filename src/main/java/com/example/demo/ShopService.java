package com.example.demo;

import com.example.demo.Item;
import com.example.demo.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 

@Service
public class ShopService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository; 

    // 생성자 주입
    public ShopService(ItemRepository itemRepository, UserRepository userRepository, InventoryRepository inventoryRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * 상점 구매 및 인벤토리 동기화 로직
     */
    @Transactional
    public PurchaseResponse purchaseItem(String currentUid, PurchaseRequest request) { 
        // 1. 유저 확인
        User user = userRepository.findByUid(currentUid) 
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 2. 아이템 정보 확인
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("아이템 정보가 없습니다."));

        //클라이언트가 보낸 가격이 서버의 실제 가격과 일치하는지 확인
        if (item.getPrice() != request.getPrice()) {
            throw new RuntimeException("아이템 가격 정보가 서버와 일치하지 않습니다.");
        }

        // 3. 가격 및 잔액 검증
        // 획득하려는 총 수량을 고려한 서버측 계산 비용
        int totalCost = item.getPrice() * request.getAmount();
        
        // 총 비용이 현재 유저의 잔액보다 많은지 확인
        if (user.getGold() < totalCost) {
            throw new RuntimeException("잔액이 부족하여 아이템을 구매할 수 없습니다.");
        }

        // 데이터 정합성 확인 (클라이언트가 알고 있는 잔액과 서버의 실제 잔액이 맞는지)
        if (!user.getGold().equals(request.getBalance())) {
            throw new RuntimeException("잔액 데이터 부정합이 발생했습니다. 다시 시도해주세요.");
        }

        // 4. 재화 차감 및 저장
        user.setGold(user.getGold() - totalCost);
        userRepository.save(user);

        // 5. 인벤토리 업데이트
        Inventory inventory = inventoryRepository.findByUserAndItemId(user, item.getItemId())
                .map(inv -> {
                    inv.setCount(inv.getCount() + request.getAmount());
                    return inv;
                })
                .orElseGet(() -> new Inventory(user, item, request.getAmount()));
        
        inventoryRepository.save(inventory);

        // 6. 응답을 위해 유저의 전체 인벤토리 목록 조회
        List<InventoryResponse> inventoryList = inventoryRepository.findByUser(user).stream()
                .map(inv -> new InventoryResponse(
                        inv.getInstanceId(), 
                        inv.getItemId(), 
                        inv.getItemName(), 
                        inv.getCount()))
                .collect(Collectors.toList());

        // 7. 결과 반환
        return new PurchaseResponse("success", item.getItemId(), user.getGold(), inventoryList);
    }

    /**
     * 상점 아이템 가격 전체 검증 로직 (목록 대조용)
     */
    public List<ItemResponse> getMismatchedItems(List<ItemRequest> clientItems) {
        List<ItemResponse> mismatched = new ArrayList<>();

        for (ItemRequest clientItem : clientItems) {
            Optional<Item> itemOpt = itemRepository.findById(clientItem.getItemId());

            if (itemOpt.isPresent()) {
                Item serverItem = itemOpt.get();
                
                boolean isNameMismatch = !serverItem.getItemName().equals(clientItem.getItemName());
                boolean isPriceMismatch = serverItem.getPrice() != clientItem.getPrice();

                if (isNameMismatch || isPriceMismatch) {
                    mismatched.add(new ItemResponse(
                        serverItem.getItemId(), 
                        serverItem.getItemName(), 
                        serverItem.getPrice()
                    ));
                }
            } else {
                mismatched.add(new ItemResponse(clientItem.getItemId(), "Unknown Item", -1));
            }
        }
        return mismatched;
    }
}