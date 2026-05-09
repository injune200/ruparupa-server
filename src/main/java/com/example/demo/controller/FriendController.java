package com.example.demo.controller;

import com.example.demo.dto.FriendDto;
import com.example.demo.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 1. 친구 코드로 유저 조회 (검색)
    @GetMapping("/users/by-code")
    public ResponseEntity<?> lookupUserByCode(
            @RequestHeader("X-USER-ID") String currentUid,
            @RequestParam String friendCode) {
        try {
            FriendDto.FriendUserLookupResponse response = friendService.lookupUserByCode(currentUid, friendCode);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 2. 친구 신청 보내기
    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @RequestBody FriendDto.SendRequest dto) {
        try {
            FriendDto.SingleRequestResponse response = friendService.sendFriendRequest(currentUid, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 3. 받은 친구 요청 목록 조회
    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedRequests(@RequestHeader("X-USER-ID") String currentUid) {
        try {
            FriendDto.FriendRequestListResponse response = friendService.getReceivedRequests(currentUid);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 4. 보낸 친구 요청 목록 조회
    @GetMapping("/requests/sent")
    public ResponseEntity<?> getSentRequests(@RequestHeader("X-USER-ID") String currentUid) {
        try {
            FriendDto.FriendRequestListResponse response = friendService.getSentRequests(currentUid);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 5. 친구 요청 수락
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable Long requestId) {
        try {
            FriendDto.AcceptRequestResponse response = friendService.acceptFriendRequest(currentUid, requestId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 6. 친구 요청 거절
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<?> rejectFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable Long requestId) {
        try {
            FriendDto.SingleRequestResponse response = friendService.rejectFriendRequest(currentUid, requestId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 7. 친구 요청 취소
    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<?> cancelFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable Long requestId) {
        try {
            FriendDto.SingleRequestResponse response = friendService.cancelFriendRequest(currentUid, requestId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 8. 내 친구 목록 조회
    @GetMapping
    public ResponseEntity<?> getFriendList(@RequestHeader("X-USER-ID") String currentUid) {
        try {
            FriendDto.FriendListResponse response = friendService.getFriendList(currentUid);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 9. 친구 삭제 (계약서에 따라 204 No Content 반환)
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<?> deleteFriend(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable String friendUserId) {
        try {
            friendService.deleteFriend(currentUid, friendUserId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 10. 친구에게 메시지 보내기
    @PostMapping("/{friendUserId}/messages")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable String friendUserId,
            @RequestBody FriendDto.MessageRequest request) {
        try {
            FriendDto.SingleMessageResponse response = friendService.sendMessage(currentUid, friendUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 11. 특정 친구와의 메시지 목록 조회
    @GetMapping("/{friendUserId}/messages")
    public ResponseEntity<?> getMessages(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable String friendUserId) {
        try {
            FriendDto.FriendMessagesResponse response = friendService.getMessages(currentUid, friendUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 공통 에러 처리 메서드
    private ResponseEntity<FriendDto.ErrorResponse> handleError(String message) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (message.contains("NOT_FOUND")) status = HttpStatus.NOT_FOUND;
        if (message.contains("ALREADY") || message.contains("DUPLICATE")) status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(new FriendDto.ErrorResponse(message, getErrorMessage(message)));
    }

    private String getErrorMessage(String code) {
        return switch (code) {
            case "USER_NOT_FOUND" -> "존재하지 않는 사용자입니다.";
            case "SELF_CODE" -> "자신의 코드는 입력할 수 없습니다.";
            case "ALREADY_FRIENDS" -> "이미 친구인 사용자입니다.";
            case "NOT_FRIENDS" -> "친구 관계가 아닙니다.";
            default -> "요청을 처리하는 중 오류가 발생했습니다.";
        };
    }
}