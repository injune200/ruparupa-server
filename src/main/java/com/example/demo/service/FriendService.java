package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.example.demo.dto.FriendDto;
import com.example.demo.entity.FriendMessage;
import com.example.demo.entity.FriendRequest;
import com.example.demo.entity.FriendRequestStatus;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.FriendshipStatus;
import com.example.demo.exception.CustomApiException; 
import com.example.demo.exception.ErrorCode;       
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
    // 헬퍼 메서드: 엔티티 -> DTO 변환 (그대로 유지)
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

    @Transactional(readOnly = true)
    public FriendDto.MyFriendCodeResponse getMyFriendCode(String currentUid) {
        User user = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        return new FriendDto.MyFriendCodeResponse(user.getFriendCode(), formatDisplayCode(user.getFriendCode()));
    }

    @Transactional(readOnly = true)
    public FriendDto.FriendUserLookupResponse lookupUserByCode(String currentUid, String targetCode) {
        User currentUser = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        User targetUser = userRepository.findByFriendCode(targetCode)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));

        if (currentUser.equals(targetUser)) throw new CustomApiException(ErrorCode.SELF_CODE);

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

    @Transactional
    public FriendDto.SingleRequestResponse sendFriendRequest(String currentUid, FriendDto.SendRequest dto) {
        User fromUser = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findByFriendCode(dto.getFriendCode())
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));

        if (fromUser.equals(toUser)) throw new CustomApiException(ErrorCode.SELF_CODE);
        if (friendshipRepository.existsByUserAndFriend(fromUser, toUser)) throw new CustomApiException(ErrorCode.ALREADY_FRIENDS);
        if (requestRepository.existsByFromUserAndToUserAndStatus(fromUser, toUser, FriendRequestStatus.PENDING)) 
            throw new CustomApiException(ErrorCode.REQUEST_ALREADY_SENT);

        FriendRequest request = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .status(FriendRequestStatus.PENDING)
                .build();
        requestRepository.save(request);
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    @Transactional
    public FriendDto.AcceptRequestResponse acceptFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.REQUEST_NOT_FOUND));
        
        if (!request.getToUser().getUid().equals(currentUid)) throw new CustomApiException(ErrorCode.BLOCKED);
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new CustomApiException(ErrorCode.REQUEST_NOT_PENDING);

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());
        
        Friendship friendship1 = Friendship.builder().user(request.getFromUser()).friend(request.getToUser()).status(FriendshipStatus.ACCEPTED).friendsSince(LocalDateTime.now()).build();
        Friendship friendship2 = Friendship.builder().user(request.getToUser()).friend(request.getFromUser()).status(FriendshipStatus.ACCEPTED).friendsSince(LocalDateTime.now()).build();

        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);

        return new FriendDto.AcceptRequestResponse(convertToRequestDto(request), convertToFriendSummary(friendship2));
    }

    @Transactional
    public FriendDto.SingleRequestResponse rejectFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.REQUEST_NOT_FOUND));
        
        if (!request.getToUser().getUid().equals(currentUid)) throw new CustomApiException(ErrorCode.BLOCKED);
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new CustomApiException(ErrorCode.REQUEST_NOT_PENDING);

        request.setStatus(FriendRequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    @Transactional
    public FriendDto.SingleRequestResponse cancelFriendRequest(String currentUid, Long requestId) {
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.REQUEST_NOT_FOUND));
        
        if (!request.getFromUser().getUid().equals(currentUid)) throw new CustomApiException(ErrorCode.BLOCKED);
        if (request.getStatus() != FriendRequestStatus.PENDING) throw new CustomApiException(ErrorCode.REQUEST_NOT_PENDING);

        request.setStatus(FriendRequestStatus.CANCELED);
        request.setRespondedAt(LocalDateTime.now());
        return new FriendDto.SingleRequestResponse(convertToRequestDto(request));
    }

    @Transactional(readOnly = true)
    public FriendDto.FriendRequestListResponse getReceivedRequests(String currentUid) {
        User me = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        List<FriendRequest> requests = requestRepository.findByToUserAndStatus(me, FriendRequestStatus.PENDING);
        return new FriendDto.FriendRequestListResponse(requests.stream().map(this::convertToRequestDto).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public FriendDto.FriendRequestListResponse getSentRequests(String currentUid) {
        User me = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        List<FriendRequest> requests = requestRepository.findByFromUserAndStatus(me, FriendRequestStatus.PENDING);
        return new FriendDto.FriendRequestListResponse(requests.stream().map(this::convertToRequestDto).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public FriendDto.FriendListResponse getFriendList(String currentUid) {
        User me = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(me, FriendshipStatus.ACCEPTED);
        return new FriendDto.FriendListResponse(friendships.stream().map(this::convertToFriendSummary).collect(Collectors.toList()));
    }

    @Transactional
    public void deleteFriend(String currentUid, String friendUserId) {
        User me = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        User friend = userRepository.findByUid(friendUserId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.FRIEND_NOT_FOUND));

        Friendship myFriendship = friendshipRepository.findByUserAndFriend(me, friend)
                .orElseThrow(() -> new CustomApiException(ErrorCode.NOT_FRIENDS));
        Friendship friendFriendship = friendshipRepository.findByUserAndFriend(friend, me).orElse(null);
        
        friendshipRepository.delete(myFriendship);
        if (friendFriendship != null) {
            friendshipRepository.delete(friendFriendship);
        }
    }

    @Transactional
    public FriendDto.SingleMessageResponse sendMessage(String currentUid, String friendUserId, FriendDto.MessageRequest request) {
        User sender = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findByUid(friendUserId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));

        if (!friendshipRepository.existsByUserAndFriend(sender, receiver)) throw new CustomApiException(ErrorCode.NOT_FRIENDS);
        
        FriendMessage message = FriendMessage.builder()
                .fromUser(sender)
                .toUser(receiver)
                .content(request.getText())
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

    @Transactional(readOnly = true)
    public FriendDto.FriendMessagesResponse getMessages(String currentUid, String friendUserId) {
        User me = userRepository.findByUid(currentUid)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));
        User friend = userRepository.findByUid(friendUserId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.USER_NOT_FOUND));

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

        return new FriendDto.FriendMessagesResponse(msgs, null);
    }
}