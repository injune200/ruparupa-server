package com.example.demo.service;

// 친구 신청의 중복 여부, 본인 신청 여부 등을 체크하고 DB에 저장

import com.example.demo.User;
import com.example.demo.dto.FriendDto;
import com.example.demo.entity.FriendRequest;
import com.example.demo.entity.FriendRequestStatus;
import com.example.demo.repository.FriendRequestRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public FriendDto.Response sendFriendRequest(String fromUid, String targetFriendCode) {
        // 1. 발신 유저 조회
        User fromUser = userRepository.findByUid(fromUid)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 2. 수신 유저 조회 (친구 코드로)
        User toUser = userRepository.findAll().stream()
                .filter(u -> targetFriendCode.equals(u.getFriendCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 3. 본인에게 신청하는지 확인
        if (fromUser.equals(toUser)) {
            throw new RuntimeException("SELF_CODE");
        }

        // 4. 이미 친구인지 확인
        if (friendshipRepository.existsByUserAndFriend(fromUser, toUser)) {
            throw new RuntimeException("ALREADY_FRIENDS");
        }

        // 5. 이미 대기 중인 요청이 있는지 확인
        if (requestRepository.existsByFromUserAndToUserAndStatus(fromUser, toUser, FriendRequestStatus.PENDING)) {
            throw new RuntimeException("REQUEST_ALREADY_SENT");
        }

        // 6. 친구 요청 저장
        FriendRequest request = new FriendRequest();
        request.setFromUser(fromUser);
        request.setToUser(toUser);
        request.setStatus(FriendRequestStatus.PENDING);
        
        FriendRequest saved = requestRepository.save(request);

        return FriendDto.Response.builder()
                .id(saved.getId())
                .fromUserId(fromUser.getUid())
                .fromNickname(fromUser.getNickname())
                .toUserId(toUser.getUid())
                .toNickname(toUser.getNickname())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 친구 요청 수락
    @Transactional
    public FriendDto.Response acceptFriendRequest(String currentUid, Long requestId) {
        // 1. 요청 존재 여부 확인
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("REQUEST_NOT_FOUND"));

        // 2. 본인이 받은 요청이 맞는지 권한 확인
        if (!request.getToUser().getUid().equals(currentUid)) {
            throw new RuntimeException("UNAUTHORIZED_REQUEST");
        }

        // 3. 이미 수락/거절된 요청인지 확인
        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new RuntimeException("REQUEST_NOT_PENDING");
        }

        // 4. 요청 상태 업데이트
        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(java.time.LocalDateTime.now());

        // 5. 양방향(A->B, B->A) 친구 관계(Friendship) 데이터 생성 ⭐(핵심)
        com.example.demo.entity.Friendship friendship1 = new com.example.demo.entity.Friendship();
        friendship1.setUser(request.getFromUser());
        friendship1.setFriend(request.getToUser());
        friendship1.setStatus(com.example.demo.entity.FriendshipStatus.ACCEPTED);

        com.example.demo.entity.Friendship friendship2 = new com.example.demo.entity.Friendship();
        friendship2.setUser(request.getToUser());
        friendship2.setFriend(request.getFromUser());
        friendship2.setStatus(com.example.demo.entity.FriendshipStatus.ACCEPTED);

        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);

        return FriendDto.Response.builder()
                .id(request.getId())
                .fromUserId(request.getFromUser().getUid())
                .fromNickname(request.getFromUser().getNickname())
                .toUserId(request.getToUser().getUid())
                .toNickname(request.getToUser().getNickname())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }

    // 친구 요청 거절
    @Transactional
    public FriendDto.Response rejectFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("REQUEST_NOT_FOUND"));

        if (!request.getToUser().getUid().equals(currentUid)) {
            throw new RuntimeException("UNAUTHORIZED_REQUEST");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new RuntimeException("REQUEST_NOT_PENDING");
        }

        // 상태만 거절(REJECTED)로 변경하고 친구 관계 테이블은 건드리지 않음
        request.setStatus(FriendRequestStatus.REJECTED);
        request.setRespondedAt(java.time.LocalDateTime.now());

        return FriendDto.Response.builder()
                .id(request.getId())
                .fromUserId(request.getFromUser().getUid())
                .fromNickname(request.getFromUser().getNickname())
                .toUserId(request.getToUser().getUid())
                .toNickname(request.getToUser().getNickname())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public FriendDto.FriendListResponse getFriendList(String currentUid) {
        // 1. 현재 유저 조회
        User currentUser = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 2. 내 친구 관계 리스트 가져오기 (상태가 ACCEPTED인 것만)
        java.util.List<com.example.demo.entity.Friendship> friendships = 
                friendshipRepository.findByUserAndStatus(currentUser, com.example.demo.entity.FriendshipStatus.ACCEPTED);

        // 3. Entity를 DTO로 변환
        java.util.List<FriendDto.FriendSummary> friendSummaries = friendships.stream()
                .map(friendship -> {
                    User friendUser = friendship.getFriend();
                    
                    // 화면 표시용 코드 포맷팅 (LUPAABCDE -> LUPA-ABCDE)
                    String displayCode = friendUser.getFriendCode();
                    if (displayCode != null && displayCode.length() > 4) {
                        displayCode = displayCode.substring(0, 4) + "-" + displayCode.substring(4);
                    }

                    FriendDto.FriendUserDto friendUserDto = FriendDto.FriendUserDto.builder()
                            .userId(friendUser.getUid())
                            .nickname(friendUser.getNickname())
                            .friendCode(friendUser.getFriendCode())
                            .displayFriendCode(displayCode)
                            .avatarAssetKey(null) // 현재는 프로필 기능이 없으므로 null
                            .build();

                    return FriendDto.FriendSummary.builder()
                            .friendshipId("friendship_" + friendship.getId())
                            .user(friendUserDto)
                            .status(friendship.getStatus())
                            .friendsSince(friendship.getFriendsSince())
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());

        return new FriendDto.FriendListResponse(friendSummaries);
    }
}