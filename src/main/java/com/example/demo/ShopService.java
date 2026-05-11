package com.example.demo;

import com.example.demo.Item;
import com.example.demo.ItemRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ItemRepository itemRepository;

    // 생성자 주입
    public ShopService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

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