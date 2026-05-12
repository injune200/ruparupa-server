package com.example.demo.controller;

import com.example.demo.dto.FriendDto;
import com.example.demo.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // ⭐ 1. 내 친구 코드 조회 (추가됨 - 404 에러 해결 지점)
    // 호출 주소: GET http://localhost:8080/friends/users/me/friend-code
    @GetMapping("/users/me/friend-code")
    public ResponseEntity<FriendDto.MyFriendCodeResponse> getMyFriendCode(
            @RequestAttribute("currentUid") String currentUid) {
        return ResponseEntity.ok(friendService.getMyFriendCode(currentUid));
    }

    // ⭐ 2. 친구 코드로 타인 유저 조회 (주소 수정됨)
    // 호출 주소: GET http://localhost:8080/friends/users/by-code?friendCode=XXXX
    @GetMapping("/users/by-code")
    public ResponseEntity<FriendDto.FriendUserLookupResponse> lookupUserByCode(
            @RequestAttribute("currentUid") String currentUid,
            @RequestParam String friendCode) {
        return ResponseEntity.ok(friendService.lookupUserByCode(currentUid, friendCode));
    }

    // 3. 친구 신청 보내기
    @PostMapping("/requests")
    public ResponseEntity<FriendDto.SingleRequestResponse> sendFriendRequest(
            @RequestAttribute("currentUid") String currentUid,
            @RequestBody FriendDto.SendRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(friendService.sendFriendRequest(currentUid, dto));
    }

    // 4. 받은 친구 요청 목록 조회
    @GetMapping("/requests/received")
    public ResponseEntity<FriendDto.FriendRequestListResponse> getReceivedRequests(@RequestAttribute("currentUid") String currentUid) {
        return ResponseEntity.ok(friendService.getReceivedRequests(currentUid));
    }

    // 5. 보낸 친구 요청 목록 조회
    @GetMapping("/requests/sent")
    public ResponseEntity<FriendDto.FriendRequestListResponse> getSentRequests(@RequestAttribute("currentUid") String currentUid) {
        return ResponseEntity.ok(friendService.getSentRequests(currentUid));
    }

    // 6. 친구 요청 수락
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<FriendDto.AcceptRequestResponse> acceptFriendRequest(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.acceptFriendRequest(currentUid, requestId));
    }

    // 7. 친구 요청 거절
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<FriendDto.SingleRequestResponse> rejectFriendRequest(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.rejectFriendRequest(currentUid, requestId));
    }

    // 8. 친구 요청 취소
    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<FriendDto.SingleRequestResponse> cancelFriendRequest(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.cancelFriendRequest(currentUid, requestId));
    }

    // 9. 내 친구 목록 조회
    @GetMapping
    public ResponseEntity<FriendDto.FriendListResponse> getFriendList(@RequestAttribute("currentUid") String currentUid) {
        return ResponseEntity.ok(friendService.getFriendList(currentUid));
    }

    // 10. 친구 삭제
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> deleteFriend(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable String friendUserId) {
        friendService.deleteFriend(currentUid, friendUserId);
        return ResponseEntity.noContent().build();
    }

    // 11. 친구에게 메시지 보내기
    @PostMapping("/{friendUserId}/messages")
    public ResponseEntity<FriendDto.SingleMessageResponse> sendMessage(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable String friendUserId,
            @RequestBody FriendDto.MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(friendService.sendMessage(currentUid, friendUserId, request));
    }

    // 12. 특정 친구와의 메시지 목록 조회
    @GetMapping("/{friendUserId}/messages")
    public ResponseEntity<FriendDto.FriendMessagesResponse> getMessages(
            @RequestAttribute("currentUid") String currentUid,
            @PathVariable String friendUserId) {
        return ResponseEntity.ok(friendService.getMessages(currentUid, friendUserId));
    }
}