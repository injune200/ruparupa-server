package com.example.roomlook.service;

import com.example.roomlook.dto.*;
import com.example.roomlook.entity.*;
import com.example.roomlook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlazaService {

    @Autowired private PlazaRepository plazaRepository;
    @Autowired private PlazaParticipantRepository participantRepository;
    @Autowired private PetRepository petRepository;

    /**
     * 1 & 2. 광장 입장 (랜덤/코드 통합)
     */
    @Transactional
    public PlazaRoomResponse joinPlaza(Long userId, String nickname, String inputCode) {
        String normalizedCode = (inputCode == null) ? "" : inputCode.replace("-", "").toUpperCase();
        if (!normalizedCode.isEmpty() && !normalizedCode.startsWith("PZ")) {
            normalizedCode = "PZ" + normalizedCode;
        }

        Plaza targetPlaza;
        if (normalizedCode.isEmpty()) {
            targetPlaza = plazaRepository.findAll().stream()
                    .filter(p -> participantRepository.countByPlaza(p) < 4)
                    .findFirst()
                    .orElseGet(this::createNewPlaza);
        } else {
            String pureCode = normalizedCode.substring(2);
            targetPlaza = plazaRepository.findByPlazaCode(pureCode)
                    .orElseThrow(() -> new RuntimeException("PLAZA_NOT_FOUND"));

            if (participantRepository.countByPlaza(targetPlaza) >= 4) {
                throw new RuntimeException("PLAZA_FULL");
            }
        }

        participantRepository.findByUserId(userId).ifPresent(p -> {
            Plaza oldPlaza = p.getPlaza();
            participantRepository.delete(p);
            if (participantRepository.countByPlaza(oldPlaza) == 0) {
                plazaRepository.delete(oldPlaza);
            }
        });

        Pet pet = petRepository.findByOwnerUserId(userId)
                .orElseThrow(() -> new RuntimeException("PET_NOT_FOUND"));

        PlazaParticipant participant = new PlazaParticipant();
        participant.setPlaza(targetPlaza);
        participant.setUserId(userId);
        participant.setNickname(nickname);
        participant.setPetId(pet.getId());
        participant.setJoinedAtMillis(System.currentTimeMillis());
        participant.setPositionX(0.5f);
        participant.setPositionY(0.5f);
        participant.setLastUpdatedAtMillis(System.currentTimeMillis());
        participantRepository.save(participant);

        targetPlaza.setRoomRevision(targetPlaza.getRoomRevision() + 1);

        return getPlazaSnapshot(targetPlaza.getPlazaId(), userId);
    }

    /**
     * 3. [추가됨] 현재 사용자의 활성 광장 조회 (MVP)
     * 이 메서드가 없어서 컨트롤러에서 오류가 났던 것입니다.
     */
    @Transactional(readOnly = true)
    public PlazaRoomResponse getCurrentPlazaMvp(Long userId) {
        return participantRepository.findByUserId(userId)
                .map(p -> getPlazaSnapshot(p.getPlaza().getPlazaId(), userId))
                .orElse(null); // 참여 중인 광장이 없으면 null 반환
    }

    /**
     * 4. 광장 스냅샷 조회 (Polling용)
     */
    @Transactional(readOnly = true)
    public PlazaRoomResponse getPlazaSnapshot(String plazaId, Long currentUserId) {
        Plaza plaza = plazaRepository.findByPlazaId(plazaId)
                .orElseThrow(() -> new RuntimeException("PLAZA_NOT_FOUND"));

        boolean isParticipant = plaza.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(currentUserId));
        if (!isParticipant) {
            throw new RuntimeException("NOT_IN_PLAZA");
        }

        PlazaRoomResponse.PlazaDetail detail = new PlazaRoomResponse.PlazaDetail();
        detail.setPlazaId(plaza.getPlazaId());
        detail.setPlazaCode("PZ" + plaza.getPlazaCode());
        detail.setDisplayPlazaCode("PZ-" + plaza.getPlazaCode());
        detail.setMaxParticipants(4);
        detail.setRoomRevision(plaza.getRoomRevision());

        detail.setParticipants(plaza.getParticipants().stream().map(p -> {
            Pet pet = petRepository.findById(p.getPetId()).orElse(null);
            return PlazaParticipantResponse.builder()
                    .userId(p.getUserId().toString())
                    .nickname(p.getNickname())
                    .profileImageUrl("")
                    .pet(PlazaParticipantResponse.PlazaPetSnapshotResponse.builder()
                            .characterAssetKey(pet != null ? pet.getCharacterAssetKey() : "UNKNOWN")
                            .appearance(1.0)
                            .build())
                    .position(PlazaParticipantResponse.PlazaPositionResponse.builder()
                            .x(p.getPositionX())
                            .y(p.getPositionY())
                            .build())
                    .build();
        }).collect(Collectors.toList()));

        detail.setMessages(new ArrayList<>());

        PlazaRoomResponse response = new PlazaRoomResponse();
        response.setPlaza(detail);
        return response;
    }

    /**
     * 5. 광장 퇴장
     */
    @Transactional
    public void leavePlaza(String plazaId, Long userId) {
        PlazaParticipant participant = participantRepository.findByUserId(userId)
                .filter(p -> p.getPlaza().getPlazaId().equals(plazaId))
                .orElseThrow(() -> new RuntimeException("NOT_IN_PLAZA"));

        Plaza plaza = participant.getPlaza();
        participantRepository.delete(participant);

        if (participantRepository.countByPlaza(plaza) == 0) {
            plazaRepository.delete(plaza);
        } else {
            plaza.setRoomRevision(plaza.getRoomRevision() + 1);
        }
    }

    /**
     * 6. 광장 채팅 전송
     */
    @Transactional
    public PlazaChatMessageResponse sendPlazaMessage(String plazaId, Long userId, String text) {
        PlazaParticipant participant = participantRepository.findByUserId(userId)
                .filter(p -> p.getPlaza().getPlazaId().equals(plazaId))
                .orElseThrow(() -> new RuntimeException("NOT_IN_PLAZA"));

        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("EMPTY_MESSAGE");
        }
        if (text.length() > 120) {
            throw new RuntimeException("MESSAGE_TOO_LONG");
        }

        participant.getPlaza().setRoomRevision(participant.getPlaza().getRoomRevision() + 1);

        return PlazaChatMessageResponse.builder()
                .id("msg_" + UUID.randomUUID().toString().substring(0, 8))
                .senderUserId(userId.toString())
                .senderNickname(participant.getNickname())
                .text(text.trim())
                .sentAtMillis(System.currentTimeMillis())
                .build();
    }

    private Plaza createNewPlaza() {
        Plaza plaza = new Plaza();
        plaza.setPlazaId("plaza_" + UUID.randomUUID().toString().substring(0, 5));
        plaza.setPlazaCode(UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        plaza.setRoomRevision(1);
        plaza.setCreatedAtMillis(System.currentTimeMillis());
        return plazaRepository.save(plaza);
    }
}