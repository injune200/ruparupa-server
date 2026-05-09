package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.dto.FriendDto;
import com.example.demo.entity.FriendMessage;
import com.example.demo.entity.FriendRequest;
import com.example.demo.entity.FriendRequestStatus;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.FriendshipStatus;
import com.example.demo.repository.FriendMessageRepository;
import com.example.demo.repository.FriendRequestRepository;
import com.example.demo.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendMessageRepository messageRepository;

    // ==========================================
    // 헬퍼 메서드: 엔티티 -> DTO 변환 (코드 중복 방지)
    // ==========================================
    private String formatDisplayCode(String code) {
        if (code != null && code.length() > 4) {
            return code.substring(0, 4) + "-" + code.substring(4);
        }
        return code;
    }

    private FriendDto.FriendUser convertToUserDto(User user) {
        return FriendDto.FriendUser.builder()
                .userId(user.getUid())
                .nickname(user.getNickname())
                .friendCode(user.getFriendCode())
                .displayFriendCode(formatDisplayCode(user.getFriendCode()))
                .avatarAssetKey(null)
                .build();
    }

    private FriendDto.FriendRequest convertToRequestDto(FriendRequest request) {
        return FriendDto.FriendRequest.builder()
                .id("friend_request_" + request.getId())
                .fromUser(convertToUserDto(request.getFromUser()))
                .toUser(convertToUserDto(request.getToUser()))
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .respondedAt(request.getRespondedAt())
                .build();
    }

    private FriendDto.FriendSummary convertToFriendSummary(Friendship friendship) {
        return FriendDto.FriendSummary.builder()
                .friendshipId("friendship_" + friendship.getId())
                .user(convertToUserDto(friendship.getFriend()))
                .status(friendship.getStatus())
                .friendsSince(friendship.getFriendsSince())
                .build();
    }

    // ==========================================
    // 핵심 비즈니스 로직
    // ==========================================

    // 1. 내 친구 코드 조회
    @Transactional(readOnly = true)
    public FriendDto.MyFriendCodeResponse getMyFriendCode(String currentUid) {
        User user = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        return new FriendDto.MyFriendCodeResponse(user.getFriendCode(), formatDisplayCode(user.getFriendCode()));
    }

    // 2. 친구 코드로 유저 조회
    @Transactional(readOnly = true)
    public FriendDto.FriendUserLookupResponse lookupUserByCode(String currentUid, String targetCode) {
        User currentUser = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        User targetUser = userRepository.findByFriendCode(targetCode).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (currentUser.equals(targetUser)) throw new RuntimeException("SELF_CODE");

        FriendshipStatus status = FriendshipStatus.NONE;
        if (friendshipRepository.existsByUserAndFriend(currentUser, targetUser)) {
            status = FriendshipStatus.ACCEPTED;
        } else if (requestRepository.existsByFromUserAndToUserAndStatus(currentUser, targetUser, FriendRequestStatus.PENDING)) {
            status = FriendshipStatus.PENDING_SENT;
        } else if (requestRepository.existsByFromUserAndToUserAndStatus(targetUser, currentUser, FriendRequestStatus.PENDING)) {
            status = FriendshipStatus.PENDING_RECEIVED;
        }

        return new FriendDto.FriendUserLookupResponse(convertToUserDto(targetUser), status);
    }

    // 3. 친구 신청
    @Transactional
    public FriendDto.SingleRequestResponse sendFriendRequest(String currentUid, FriendDto.SendRequest dto) {
        User fromUser = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        User toUser = userRepository.findByFriendCode(dto.getFriendCode()).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (fromUser.equals(toUser)) throw new RuntimeException("SELF_CODE");
        if (friendshipRepository.existsByUserAndFriend(fromUser, toUser)) throw new RuntimeException("ALREADY_FRIENDS");
        if (requestRepository.existsByFromUserAndToUserAndStatus(fromUser, toUser, FriendRequestStatus.PENDING)) throw new RuntimeException("REQUEST_ALREADY_SENT");
        // 상대방이 나에게 이미 보낸 요청이 있는지도 체크하면 좋지만, 아직 미구현

        FriendRequest request = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .status(FriendRequestStatus.PENDING)
                .build();

        requestRepository.save(request);
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    // 4. 친구 수락 (md에 맞게 request와 friendship 동시 반환)
    @Transactional
    public FriendDto.AcceptRequestResponse acceptFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("REQUEST_NOT_FOUND"));
        if (!request.getToUser().getUid().equals(currentUid)) throw new RuntimeException("UNAUTHORIZED_REQUEST");
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("REQUEST_NOT_PENDING");

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());

        // 양방향 데이터 생성
        Friendship friendship1 = Friendship.builder().user(request.getFromUser()).friend(request.getToUser()).status(FriendshipStatus.ACCEPTED).friendsSince(LocalDateTime.now()).build();
        Friendship friendship2 = Friendship.builder().user(request.getToUser()).friend(request.getFromUser()).status(FriendshipStatus.ACCEPTED).friendsSince(LocalDateTime.now()).build();

        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);

        // 내 입장에서의 요약 정보(friendship2)를 반환합니다.
        return new FriendDto.AcceptRequestResponse(convertToRequestDto(request), convertToFriendSummary(friendship2));
    }

    // 5. 친구 거절
    @Transactional
    public FriendDto.SingleRequestResponse rejectFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("REQUEST_NOT_FOUND"));
        if (!request.getToUser().getUid().equals(currentUid)) throw new RuntimeException("UNAUTHORIZED_REQUEST");
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("REQUEST_NOT_PENDING");

        request.setStatus(FriendRequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    // 6. 친구 요청 취소
    @Transactional
    public FriendDto.SingleRequestResponse cancelFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("REQUEST_NOT_FOUND"));
        if (!request.getFromUser().getUid().equals(currentUid)) throw new RuntimeException("UNAUTHORIZED_REQUEST");
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("REQUEST_NOT_PENDING");

        request.setStatus(FriendRequestStatus.CANCELED);
        request.setRespondedAt(LocalDateTime.now());
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    // 7-1. 받은 요청 목록 
    @Transactional(readOnly = true)
    public FriendDto.FriendRequestListResponse getReceivedRequests(String currentUid) {
        User me = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        List<FriendRequest> requests = requestRepository.findByToUserAndStatus(me, FriendRequestStatus.PENDING);
        return new FriendDto.FriendRequestListResponse(requests.stream().map(this::convertToRequestDto).collect(Collectors.toList()));
    }

    // 7-2. 보낸 요청 목록
    @Transactional(readOnly = true)
    public FriendDto.FriendRequestListResponse getSentRequests(String currentUid) {
        User me = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        List<FriendRequest> requests = requestRepository.findByFromUserAndStatus(me, FriendRequestStatus.PENDING);
        return new FriendDto.FriendRequestListResponse(requests.stream().map(this::convertToRequestDto).collect(Collectors.toList()));
    }

    // 8. 친구 목록 조회
    @Transactional(readOnly = true)
    public FriendDto.FriendListResponse getFriendList(String currentUid) {
        User me = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(me, FriendshipStatus.ACCEPTED);
        return new FriendDto.FriendListResponse(friendships.stream().map(this::convertToFriendSummary).collect(Collectors.toList()));
    }

    // 9. 친구 삭제 (md에 맞춰 대상 유저 ID 기반으로 변경)
    @Transactional
    public void deleteFriend(String currentUid, String friendUserId) {
        User me = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        User friend = userRepository.findByUid(friendUserId).orElseThrow(() -> new RuntimeException("FRIEND_NOT_FOUND"));

        Friendship myFriendship = friendshipRepository.findByUserAndFriend(me, friend).orElseThrow(() -> new RuntimeException("NOT_FRIENDS"));
        Friendship friendFriendship = friendshipRepository.findByUserAndFriend(friend, me).orElse(null);

        friendshipRepository.delete(myFriendship);
        if (friendFriendship != null) {
            friendshipRepository.delete(friendFriendship);
        }
    }

    // 10. 메시지 보내기 (text 파라미터 적용)
    @Transactional
    public FriendDto.SingleMessageResponse sendMessage(String currentUid, String friendUserId, FriendDto.MessageRequest request) {
        User sender = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        User receiver = userRepository.findByUid(friendUserId).orElseThrow(() -> new RuntimeException("RECEIVER_NOT_FOUND"));

        if (!friendshipRepository.existsByUserAndFriend(sender, receiver)) throw new RuntimeException("NOT_FRIENDS");

        FriendMessage message = FriendMessage.builder()
                .fromUser(sender)
                .toUser(receiver)
                .content(request.getText()) // DTO의 text를 엔티티의 content에 저장
                .build();

        messageRepository.save(message);

        return new FriendDto.SingleMessageResponse(FriendDto.FriendMessage.builder()
                .id("msg_" + message.getId())
                .friendUserId(receiver.getUid())
                .senderUserId(sender.getUid())
                .text(message.getContent())
                .sentAt(message.getCreatedAt())
                .build());
    }

    // 11. 친구와의 메시지 목록 조회 (특정 친구 기반)
    @Transactional(readOnly = true)
    public FriendDto.FriendMessagesResponse getMessages(String currentUid, String friendUserId) {
        User me = userRepository.findByUid(currentUid).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        User friend = userRepository.findByUid(friendUserId).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 내가 받은 메시지 중에서, 특정 친구가 보낸 것만 필터링
        List<FriendMessage> received = messageRepository.findByToUserOrderByCreatedAtDesc(me);
        List<FriendDto.FriendMessage> msgs = received.stream()
                .filter(m -> m.getFromUser().getUid().equals(friendUserId))
                .map(m -> FriendDto.FriendMessage.builder()
                        .id("msg_" + m.getId())
                        .friendUserId(m.getFromUser().getUid()) 
                        .senderUserId(m.getFromUser().getUid())
                        .text(m.getContent())
                        .sentAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return new FriendDto.FriendMessagesResponse(msgs, null); // nextCursor는 현재는 null
    }
}