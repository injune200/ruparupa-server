package com.example.roomlook.service;

import com.example.demo.User; 
import com.example.demo.UserRepository; 
import com.example.roomlook.dto.*;
import com.example.roomlook.entity.*;
import com.example.roomlook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlazaService {

    @Autowired private PlazaRepository plazaRepository;
    @Autowired private PlazaParticipantRepository participantRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private UserRepository userRepository; 

    @Transactional
    public Map<String, Object> joinPlaza(String currentUid, String inputCode) { 
        
        // 1. UID로 유저 정보 조회하여 기존 로직에 필요한 데이터 추출
        User user = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Long userId = user.getId();
        String nickname = user.getNickname();

        String normalizedCode = inputCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (normalizedCode.startsWith("PZ") && normalizedCode.length() > 2) {
            normalizedCode = normalizedCode.substring(2);
        }

        Plaza targetPlaza;
        if (normalizedCode.isEmpty()) {
            targetPlaza = plazaRepository.findAll().stream()
                    .filter(p -> participantRepository.countByPlaza(p) < 4)
                    .findFirst()
                    .orElseGet(this::createNewPlaza);
        } else {
            targetPlaza = plazaRepository.findByPlazaCode(normalizedCode)
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
        // 좌표 Clamp 범위 내 초기화
        participant.setPositionX(0.42f);
        participant.setPositionY(0.68f);
        participant.setLastUpdatedAtMillis(System.currentTimeMillis());
        participantRepository.save(participant);

        targetPlaza.setRoomRevision(targetPlaza.getRoomRevision() + 1);
        Map<String, Object> result = new HashMap<>();
        result.put("plaza", getPlazaSnapshot(targetPlaza.getPlazaId(), userId));
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCurrentPlaza(String currentUid) { 
        
        // 1. UID로 유저 정보 조회
        User user = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Long userId = user.getId();

        Map<String, Object> result = new HashMap<>();
        participantRepository.findByUserId(userId).ifPresentOrElse(
                p -> result.put("plaza", getPlazaSnapshot(p.getPlaza().getPlazaId(), userId)),
                () -> result.put("plaza", null)
        );
        return result;
    }

    @Transactional
    public PlazaRoomResponse getPlazaSnapshot(String plazaId, Long currentUserId) {
        Plaza plaza = plazaRepository.findByPlazaId(plazaId)
                .orElseThrow(() -> new RuntimeException("PLAZA_NOT_FOUND"));

        long now = System.currentTimeMillis();

        plaza.getParticipants().forEach(p -> {
            if (p.getTargetX() != null && p.getMoveStartedAtMillis() != null) {
                if (now >= p.getMoveStartedAtMillis() + p.getMoveDurationMillis()) {
                    p.setPositionX(p.getTargetX());
                    p.setPositionY(p.getTargetY());
                    p.setTargetX(null);
                    p.setTargetY(null);
                    p.setLastUpdatedAtMillis(now);
                }
            }
        });

        PlazaRoomResponse response = new PlazaRoomResponse();
        response.setPlazaId(plaza.getPlazaId());
        response.setPlazaCode("PZ" + plaza.getPlazaCode());
        response.setDisplayPlazaCode("PZ-" + plaza.getPlazaCode());

        response.setParticipants(plaza.getParticipants().stream().map(p -> {
            PlazaParticipantResponse pr = new PlazaParticipantResponse();
            pr.setUserId(p.getUserId().toString());
            pr.setNickname(p.getNickname());
            Pet pet = petRepository.findById(p.getPetId()).get();
            pr.setPet(convertToPetSnapshot(pet));
            pr.setJoinedAtMillis(p.getJoinedAtMillis());
            pr.setPosition(new PlazaPosition(p.getPositionX(), p.getPositionY()));
            pr.setMovement(null);
            pr.setPositionUpdatedAtMillis(p.getLastUpdatedAtMillis());
            return pr;
        }).collect(Collectors.toList()));

        response.setMessages(new ArrayList<>());
        response.setInteractions(new ArrayList<>());
        response.setMaxParticipants(4);
        response.setRoomRevision(plaza.getRoomRevision());
        response.setServerTime(Map.of("serverNowMillis", now));
        response.setJoinedAtMillis(now);
        response.setIsServerAuthoritative(true);

        return response;
    }

    private Plaza createNewPlaza() {
        Plaza plaza = new Plaza();
        plaza.setPlazaId("plaza_" + UUID.randomUUID().toString().substring(0, 5));
        plaza.setPlazaCode(UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        plaza.setRoomRevision(1);
        plaza.setCreatedAtMillis(System.currentTimeMillis());
        return plazaRepository.save(plaza);
    }

    private PlazaPetSnapshot convertToPetSnapshot(Pet pet) {
        PlazaPetSnapshot s = new PlazaPetSnapshot();
        s.setPetId(pet.getId().toString());
        s.setOwnerUserId(pet.getOwnerUserId().toString());
        s.setName(pet.getName());
        s.setCharacterAssetKey(pet.getCharacterAssetKey());

        // 5가지 외형 배율 모두 포함
        PetAppearance app = new PetAppearance();
        app.setHeadSizeScale(pet.getHeadSizeScale());
        app.setBodySizeScale(pet.getBodySizeScale());
        app.setEyeSizeScale(1.0f);
        app.setNoseSizeScale(1.0f);
        app.setMouthSizeScale(1.0f);
        s.setAppearance(app);

        PetStatus st = new PetStatus();
        st.setSatiety(100); st.setVitality(100); st.setIsEgg(false);
        s.setStatus(st);

        s.setPersonality("ACTIVE");
        s.setEquippedItemIds(new ArrayList<>()); // 아이템 목록
        return s;
    }
}
