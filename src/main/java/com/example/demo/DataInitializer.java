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
                lupa.setName("루파");
                lupa.setHunger(100);     // 포만감 초기값 
                lupa.setEnergy(100);     // 컨디션 초기값 
                lupa.setCleanliness(100);
                lupa.setHappiness(85);
                lupa.setCurrentAction("IDLE");
                petRepo.save(lupa);
            }

            // 2. 초기 가구(침대) 데이터 삽입
            if (furnitureRepo.count() == 0) {
                RoomFurniture bed = new RoomFurniture();
                bed.setType("bed");
                bed.setX(5); // 논리 좌표 예시
                bed.setY(5);
                bed.setRoomId(1L);
                furnitureRepo.save(bed);
            }
        };
    }
}