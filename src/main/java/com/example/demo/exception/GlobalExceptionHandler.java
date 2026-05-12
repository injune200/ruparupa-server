package com.example.demo.exception;

import com.example.demo.dto.FriendDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 우리가 만든 커스텀 에러 (친구 API 등)
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<FriendDto.ErrorResponse> handleCustomApiException(CustomApiException e) {
        FriendDto.ErrorResponse errorResponse = FriendDto.ErrorResponse.builder()
                .code(e.getErrorCode().name())
                .message(e.getErrorCode().getMessage())
                .build();

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<FriendDto.ErrorResponse> handleStandardExceptions(RuntimeException e) {
        FriendDto.ErrorResponse errorResponse = FriendDto.ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(e.getMessage()) 
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}