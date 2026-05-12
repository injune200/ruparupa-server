package com.example.roomlook.repository;

import com.example.roomlook.entity.PlazaChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlazaChatMessageRepository extends JpaRepository<PlazaChatMessage, Long> {
    // 최신 50개 메시지만 유지하기 위해 보낸 시간순으로
    List<PlazaChatMessage> findTop50ByPlazaIdOrderBySentAtMillisDesc(String plazaId);
}
//광장에서 오간 채팅 저장 및 관리