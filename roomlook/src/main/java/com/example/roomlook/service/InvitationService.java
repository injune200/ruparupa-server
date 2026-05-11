package com.example.roomlook.service;

// 1. 필요한 부품들을 가져오는 선언 (Import) - 이 부분이 빠져서 빨간 줄이 뜨는 겁니다!
import com.example.roomlook.entity.Invitation;
import com.example.roomlook.entity.Furniture;
import com.example.roomlook.repository.InvitationRepository;
import com.example.roomlook.dto.HomeSnapshot; // 아까 만든 DTO 폴더 안의 파일
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List; // List를 쓰기 위해 꼭 필요함

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private FurnitureService furnitureService;

    // 초대 보내기 로직
    public String sendInvitation(Long senderId, Long receiverId, Long roomId) {
        if (invitationRepository.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "PENDING").isPresent()) {
            return "ALREADY_SENT";
        }

        Invitation invitation = new Invitation();
        invitation.setSenderId(senderId);
        invitation.setReceiverId(receiverId);
        invitation.setRoomId(roomId);
        invitation.setStatus("PENDING");
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusHours(24));

        invitationRepository.save(invitation);
        return "SUCCESS";
    }

    // 초대 수락하기 로직
    public HomeSnapshot acceptInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("HOME_INVITATION_NOT_FOUND"));

        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);

        String hostUid = String.valueOf(invitation.getSenderId());
        List<Furniture> furnitureList = furnitureService.getFurnitureList(hostUid);

        HomeSnapshot.RoomSnapshot roomBox = new HomeSnapshot.RoomSnapshot(furnitureList);

        return new HomeSnapshot(roomBox);
    }
}