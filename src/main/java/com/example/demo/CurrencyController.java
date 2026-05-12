package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor 
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * 재화 획득 및 검증 API
     */
    @PostMapping("/earn")
    public ResponseEntity<?> gainGold(
            @RequestAttribute("currentUid") String currentUid,
            @RequestBody GoldRequest goldRequest) {
        
        try {
            Long updatedGold = currencyService.gainGoldWithVerification(
                currentUid, // nickname 대신 안전한 currentUid 사용
                goldRequest.getAmount(), 
                goldRequest.getTotal()
            );
            return ResponseEntity.ok("검증 완료 및 재화 획득 성공! 현재 잔액: " + updatedGold);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("검증 에러: " + e.getMessage());
        }
    }
}

class GoldRequest {
    private int amount;
    private Long total;

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
}