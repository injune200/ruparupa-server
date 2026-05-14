package com.example.demo.exception;
 
import com.example.demo.dto.FriendDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@RestControllerAdvice
public class GlobalExceptionHandler {
 
    // 1. 커스텀 에러 (친구 API 등)
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
 
    // 2. IllegalStateException / IllegalArgumentException
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
 
    // 3. 그 외 분류되지 않은 모든 예외 → UNKNOWN으로 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FriendDto.ErrorResponse> handleUnknownException(Exception e) {
        FriendDto.ErrorResponse errorResponse = FriendDto.ErrorResponse.builder()
                .code(ErrorCode.UNKNOWN.name())
                .message(ErrorCode.UNKNOWN.getMessage())
                .build();
 
        return ResponseEntity
                .status(ErrorCode.UNKNOWN.getStatus())
                .body(errorResponse);
    }
}