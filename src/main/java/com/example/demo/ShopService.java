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
            // DB에서 아이템 아이디로 정보를 조회합니다.
            Optional<Item> itemOpt = itemRepository.findById(clientItem.getItemId());

            if (itemOpt.isPresent()) {
                Item serverItem = itemOpt.get();
                // DB의 진짜 가격과 클라이언트가 보낸 가격을 비교합니다.
                if (serverItem.getPrice() != clientItem.getPrice()) {
                    mismatched.add(new ItemResponse(serverItem.getItemId(), serverItem.getPrice()));
                }
            } else {
                // DB에 없는 아이템 아이디인 경우 -1로 응답 처리
                mismatched.add(new ItemResponse(clientItem.getItemId(), -1));
            }
        }
        return mismatched;
    }
}