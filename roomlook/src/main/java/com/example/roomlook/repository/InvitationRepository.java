package com.example.roomlook.repository;

import com.example.roomlook.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    //아직 대기 중인 초대가 있는지 확인
    Optional<Invitation> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);
}