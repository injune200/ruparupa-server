package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final JwtUtil jwtUtil;

    public CurrencyController(CurrencyService currencyService, JwtUtil jwtUtil) {
        this.currencyService = currencyService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/earn")
    public ResponseEntity<?> gainGold(@RequestBody GoldRequest goldRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("status", "fail", "message", "인증 토큰이 없습니다."));
        }

        String token = authHeader.substring(7);
        Map<String, Object> response = new HashMap<>();
        
        try {
            String nickname = jwtUtil.extractNickname(token); 

            // 재화 지급 및 검증 실행
            Long updatedGold = currencyService.gainGoldWithVerification(
                nickname, 
                goldRequest.getAmount(), 
                goldRequest.getTotal()
            );

            // 성공 응답: { "status": "success" }
            response.put("status", "success");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // 검증 실패 시: { "status": "fail", "total": 현재서버잔액 }
            // 서비스에서 현재 잔액을 함께 던져주도록 설계하거나, 여기서 조회할 수 있습니다.
            response.put("status", "fail");
            
            // 에러 메시지에 포함된 현재 잔액 정보를 파싱하거나 별도 조회 필요
            // 여기서는 흐름상 실패 상태와 현재 (조작 전) 잔액을 반환하도록 구성합니다.
            response.put("total", currencyService.getCurrentGold(jwtUtil.extractNickname(token)));
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            response.put("status", "fail");
            return ResponseEntity.status(403).body(response);
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