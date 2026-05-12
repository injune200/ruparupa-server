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
    // 생성자 주입 (의존성 추가)
    public ShopService(ItemRepository itemRepository, UserRepository userRepository, InventoryRepository inventoryRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * 상점 구매 및 인벤토리 동기화 로직
     */
    @Transactional
    public PurchaseResponse purchaseItem(String nickname, PurchaseRequest request) {
        // 1. 유저 확인
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 2. 아이템 정보 확인
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("아이템 정보가 없습니다."));

        // 3. 가격 및 잔액 검증
        int totalCost = item.getPrice() * request.getAmount();
        
        // 클라이언트가 보낸 잔액과 서버의 실제 잔액이 맞는지, 그리고 살 돈이 있는지 체크
        if (user.getGold() < totalCost || !user.getGold().equals(request.getBalance())) {
            throw new RuntimeException("잔액이 부족하거나 데이터 부정합이 발생했습니다.");
        }

        // 4. 재화 차감 및 저장
        user.setGold(user.getGold() - totalCost);
        userRepository.save(user);

        // 5. 인벤토리 업데이트 (이미 같은 아이템이 있다면 수량만 증가, 없으면 새로 생성)
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
     * 기존 코드: 상점 아이템 가격 검증 로직
     */
    public List<ItemResponse> getMismatchedItems(List<ItemRequest> clientItems) {
        List<ItemResponse> mismatched = new ArrayList<>();

        for (ItemRequest clientItem : clientItems) {
            Optional<Item> itemOpt = itemRepository.findById(clientItem.getItemId());

            if (itemOpt.isPresent()) {
                Item serverItem = itemOpt.get();
                
                // 이름 또는 가격이 하나라도 다르면 검증 실패
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
                // DB에 없는 아이템인 경우
                mismatched.add(new ItemResponse(clientItem.getItemId(), "Unknown Item", -1));
            }
        }
        return mismatched;
    }
}