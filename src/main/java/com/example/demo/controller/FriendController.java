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

    // 친구 신청 보내기 API
    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(
            // md에는 JWT 토큰을 쓴다고 되어있으나 
            // 테스트를 위해 HTTP 헤더(X-USER-ID)로 발신자의 UID를 임시로 받고 있음. 추후 수정
            @RequestHeader("X-USER-ID") String fromUid,
            @RequestBody FriendDto.Request request) {

        try {
            // 서비스 로직 실행 (성공 시 200 OK와 함께 데이터 반환)
            FriendDto.Response response = friendService.sendFriendRequest(fromUid, request.getFriendCode());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // 서비스 로직에서 던진 에러(예: USER_NOT_FOUND)를 캐치해서 변환
            return handleError(e.getMessage());
        }
    }

    // 친구 요청 수락 API
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable Long id) { // URL의 {id} 값을 가져옵니다.
        try {
            FriendDto.Response response = friendService.acceptFriendRequest(currentUid, id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 친구 요청 거절 API
    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<?> rejectFriendRequest(
            @RequestHeader("X-USER-ID") String currentUid,
            @PathVariable Long id) {
        try {
            FriendDto.Response response = friendService.rejectFriendRequest(currentUid, id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // 친구 목록 조회 API
    @GetMapping // @RequestMapping("/friends") 가 클래스 위에 있으므로 경로는 GET /friends 가 됨
    public ResponseEntity<?> getFriendList(@RequestHeader("X-USER-ID") String currentUid) {
        try {
            FriendDto.FriendListResponse response = friendService.getFriendList(currentUid);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return handleError(e.getMessage());
        }
    }

    // MD에 명시된 에러 코드와 HTTP 상태 코드를 매핑해주는 헬퍼 메서드
    private ResponseEntity<FriendDto.ErrorResponse> handleError(String errorCode) {
        HttpStatus status;
        String message;

        switch (errorCode) {
            case "USER_NOT_FOUND":
                status = HttpStatus.NOT_FOUND; // 404
                message = "친구 코드에 해당하는 유저가 없습니다.";
                break;
            case "SELF_CODE":
                status = HttpStatus.BAD_REQUEST; // 400
                message = "본인의 친구 코드는 입력할 수 없습니다.";
                break;
            case "ALREADY_FRIENDS":
                status = HttpStatus.CONFLICT; // 409
                message = "이미 친구인 유저입니다.";
                break;
            case "REQUEST_ALREADY_SENT":
                status = HttpStatus.CONFLICT; // 409
                message = "이미 보낸 친구 요청이 있습니다.";
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
                errorCode = "UNKNOWN_ERROR";
                message = "알 수 없는 에러가 발생했습니다.";
            case "REQUEST_NOT_FOUND":
                status = HttpStatus.NOT_FOUND; // 404
                message = "친구 요청을 찾을 수 없습니다.";
                break;
            case "REQUEST_NOT_PENDING":
                status = HttpStatus.CONFLICT; // 409
                message = "이미 처리된 친구 요청입니다.";
                break;
            case "UNAUTHORIZED_REQUEST":
                status = HttpStatus.FORBIDDEN; // 403
                message = "해당 요청을 처리할 권한이 없습니다.";
                break;
        }

        return ResponseEntity
                .status(status)
                .body(new FriendDto.ErrorResponse(errorCode, message));
    }
}