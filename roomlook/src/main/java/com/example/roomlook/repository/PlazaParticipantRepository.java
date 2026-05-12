package com.example.roomlook.repository;

import com.example.roomlook.entity.Plaza;
import com.example.roomlook.entity.PlazaParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlazaParticipantRepository extends JpaRepository<PlazaParticipant, Long> {
    //유저는 동시에 하나의 광장에만 소속된다를 체크하기 위해 필요
    Optional<PlazaParticipant> findByUserId(Long userId);
    long countByPlaza(Plaza plaza);
}
//어느 유저가 어떤 광장에 접속해 있는지 관리