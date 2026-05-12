package com.example.roomlook.service;

import com.example.roomlook.entity.Furniture;
import com.example.roomlook.repository.FurnitureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FurnitureService {

    private final FurnitureRepository furnitureRepository;

    // 초대 수락 시 사용할 가구 리스트 불러오기 함수
    public List<Furniture> getFurnitureList(String uid) {
        return furnitureRepository.findByUid(uid);
    }

    @Transactional
    public void saveRoomLayout(String uid, List<Furniture> newLayout) {
        validateOverlap(newLayout);
        furnitureRepository.deleteByUid(uid);
        for (Furniture f : newLayout) {
            f.setUid(uid);
            furnitureRepository.save(f);
        }
    }

    private void validateOverlap(List<Furniture> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Furniture a = list.get(i);
                Furniture b = list.get(j);
                boolean isOverlapping = a.getX() < b.getX() + b.getWidth() &&
                        a.getX() + a.getWidth() > b.getX() &&
                        a.getY() < b.getY() + b.getHeight() &&
                        a.getY() + a.getHeight() > b.getY();

                if (isOverlapping) {
                    throw new RuntimeException("가구가 서로 겹칩니다: " + a.getFurnitureId() + " & " + b.getFurnitureId());
                }
            }
        }
    }
}