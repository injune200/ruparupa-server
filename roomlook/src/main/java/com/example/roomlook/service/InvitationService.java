package com.example.roomlook.service;

import com.example.roomlook.dto.AcceptHomeInvitationResponse;
import com.example.roomlook.dto.HomeSnapshot;
import com.example.roomlook.entity.Furniture;
import com.example.roomlook.entity.Invitation;
import com.example.roomlook.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private FurnitureService furnitureService;

    // 1. 초대 보내기
    @Transactional
    public Invitation sendInvitation(Long senderId, Long receiverId, Long roomId, String message) {
        // 중복 초대 체크
        if (invitationRepository.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "PENDING").isPresent()) {
            throw new RuntimeException("HOME_INVITATION_ALREADY_SENT");
        }

        Invitation invitation = new Invitation();
        invitation.setSenderId(senderId);
        invitation.setReceiverId(receiverId);
        invitation.setRoomId(roomId);
        invitation.setMessage(message); // 민영님이 요청한 메시지 저장!
        invitation.setStatus("PENDING");
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusHours(24));

        return invitationRepository.save(invitation);
    }

    // 2. 초대 수락
    @Transactional
    public AcceptHomeInvitationResponse acceptInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("HOME_INVITATION_NOT_FOUND"));

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new RuntimeException("HOME_INVITATION_NOT_PENDING");
        }

        invitation.setStatus("ACCEPTED");
        invitation.setRespondedAt(LocalDateTime.now()); // 응답 시간 기록
        invitationRepository.save(invitation);

        // 친구 집 가구 정보 가져오기
        String hostUid = String.valueOf(invitation.getSenderId());
        List<Furniture> furnitureList = furnitureService.getFurnitureList(hostUid);

        // 포장지에 담기
        HomeSnapshot.RoomSnapshot roomBox = new HomeSnapshot.RoomSnapshot(furnitureList);
        HomeSnapshot homeSnapshot = new HomeSnapshot(roomBox);

        // 최종 계약서 규격 박스에 담음
        return new AcceptHomeInvitationResponse(invitation, homeSnapshot);
    }

    // 3. 초대 거절
    @Transactional
    public Invitation rejectInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("HOME_INVITATION_NOT_FOUND"));

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new RuntimeException("HOME_INVITATION_NOT_PENDING");
        }

        invitation.setStatus("REJECTED");
        invitation.setRespondedAt(LocalDateTime.now()); // 응답 시간 기록

        return invitationRepository.save(invitation);
    }
}