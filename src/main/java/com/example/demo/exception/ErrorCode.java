package com.example.demo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMPTY_CODE(400, "친구 코드가 비어 있습니다."),
    SELF_CODE(400, "자신의 코드는 입력할 수 없습니다."),
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    ALREADY_FRIENDS(409, "이미 친구인 유저입니다."),
    REQUEST_ALREADY_SENT(409, "이미 내가 보낸 친구 요청이 있습니다."),
    REQUEST_ALREADY_RECEIVED(409, "이미 상대에게서 받은 요청이 있습니다."),
    REQUEST_NOT_FOUND(404, "친구 요청을 찾을 수 없습니다."),
    REQUEST_NOT_PENDING(409, "이미 처리된 친구 요청입니다."),
    FRIEND_NOT_FOUND(404, "친구 정보를 찾을 수 없습니다."),
    NOT_FRIENDS(403, "친구 관계가 아니라 접근할 수 없습니다."),
    BLOCKED(403, "접근 권한이 없습니다."), 
    UNKNOWN(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;
}