package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final JwtUtil jwtUtil;

    public CurrencyController(CurrencyService currencyService, JwtUtil jwtUtil) {
        this.currencyService = currencyService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 재화 획득 및 검증 API
     * 클라이언트가 보낸 (기존 잔액 + 획득량)이 서버의 계산 결과와 일치하는지 확인합니다.
     */
    @PostMapping("/gain")
    public ResponseEntity<?> gainGold(@RequestBody GoldRequest goldRequest, HttpServletRequest request) {
        // 1. 헤더에서 JWT 추출 (Bearer 토큰)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("인증 토큰이 없습니다.");
        }

        String token = authHeader.substring(7);
        
        try {
            // 2. 토큰에서 닉네임 추출 (JwtUtil 활용)
            String nickname = jwtUtil.extractNickname(token); 

            // 3. 재화 지급 및 검증 로직 실행
            // 서비스에서 서버 DB 잔액 + amount == goldRequest.getTotal() 인지 검사합니다.
            Long updatedGold = currencyService.gainGoldWithVerification(
                nickname, 
                goldRequest.getAmount(), 
                goldRequest.getTotal()
            );

            return ResponseEntity.ok("검증 완료 및 재화 획득 성공! 현재 잔액: " + updatedGold);

        } catch (RuntimeException e) {
            // 검증 실패(데이터 부정합) 또는 사용자를 찾을 수 없는 경우
            return ResponseEntity.status(400).body("검증 에러: " + e.getMessage());
        } catch (Exception e) {
            // 토큰 위변조 또는 기타 서버 에러
            return ResponseEntity.status(403).body("유효하지 않은 요청입니다.");
        }
    }
}

/**
 * 재화 획득 요청을 위한 데이터 전달 객체 (DTO)
 */
class GoldRequest {
    private int amount; // 이번에 획득한 재화의 양
    private Long total;  // 클라이언트 측에서 계산한 최종 예상 잔액 (기존 잔액 + amount)

    // Getter & Setter
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
}