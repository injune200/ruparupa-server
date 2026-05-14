package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor 
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping("/earn")
    public ResponseEntity<?> gainGold(
            @RequestAttribute("currentUid") String currentUid, 
            @RequestBody GoldRequest goldRequest) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 인터셉터가 검증한 currentUid를 바로 사용
            Long updatedGold = currencyService.gainGoldWithVerification(
                currentUid, 
                goldRequest.getAmount(), 
                goldRequest.getTotal()
            );
            
            response.put("status", "success");
            response.put("total", updatedGold);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Long currentGold = currencyService.getCurrentGold(currentUid);
            response.put("status", "fail");
            response.put("total", currentGold); // 실패 시 현재 서버 잔액 반환
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Long currentGold = currencyService.getCurrentGold(currentUid);
            response.put("status", "fail");
            response.put("total", currentGold);
            return ResponseEntity.status(403).body(response);
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