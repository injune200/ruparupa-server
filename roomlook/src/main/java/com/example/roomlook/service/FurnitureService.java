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

    @Transactional
    public void saveRoomLayout(String uid, List<Furniture> newLayout) {
        // 1. 가구 간 겹침 검증
        validateOverlap(newLayout);

        // 2. 기존 배치 삭제 (항상 최신 상태로 덮어쓰기)
        furnitureRepository.deleteByUid(uid);

        // 3. 새로운 배치 저장
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

                // 사각형 충돌 검사 알고리즘
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