package com.example.demo;

import com.example.demo.entity.Pet;
import com.example.demo.entity.RoomFurniture;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.RoomFurnitureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(PetRepository petRepo, RoomFurnitureRepository furnitureRepo) {
        return args -> {
            // 1. 초기 펫 데이터 삽입
            if (petRepo.count() == 0) {
                Pet lupa = new Pet();
                lupa.setUid("test_uid_123"); // 새로 추가된 uid
                lupa.setName("루파");
                lupa.setHunger(100);     // 포만감 초기값 
                lupa.setEnergy(100);     // 컨디션 초기값 
                lupa.setCleanliness(100);
                lupa.setHappiness(85);
                lupa.setCurrentAction("IDLE");
                
                // --- 새로 추가된 외형 및 속성 테스트 데이터 ---
                lupa.setHeadSize(3);
                lupa.setBodySize(3);
                lupa.setEyeDesign(1);
                lupa.setNoseDesign(1);
                lupa.setMouthDesign(1);
                lupa.setEgg(true);
                lupa.setPersonality("활발");
                lupa.setDecoration("None");
                
                petRepo.save(lupa);
            }

            // 2. 초기 가구(침대) 데이터 삽입
            if (furnitureRepo.count() == 0) {
                RoomFurniture bed = new RoomFurniture();
                bed.setType("bed");
                bed.setX(5); // 논리 좌표 예시
                bed.setY(5);
                bed.setRoomId(1L);
                
                // --- 가구 방향/상태 테스트 데이터 ---
                bed.setDirection(0); // 0도 (기본 방향)
                bed.setStatus("unused"); // 사용 안 함
                
                furnitureRepo.save(bed);
            }
        };
    }
}