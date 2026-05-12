package com.example.roomlook.repository;

import com.example.roomlook.entity.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {
    //광장 ID나 입장 코드로 방을 찾기 위해 필요.
    Optional<Plaza> findByPlazaId(String plazaId);
    Optional<Plaza> findByPlazaCode(String plazaCode);
}
//광장 방 정보를 찾고 저장하는 역할