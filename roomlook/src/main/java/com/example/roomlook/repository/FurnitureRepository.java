package com.example.roomlook.repository;

import com.example.roomlook.entity.Furniture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FurnitureRepository extends JpaRepository<Furniture, Long> {
    // 특정 유저의 방에 배치된 모든 가구 리스트를 가져오는 기능
    List<Furniture> findByUid(String uid);

    // 저장하기 전에 기존 배치를 지우기 위한 기능
    void deleteByUid(String uid);
}
