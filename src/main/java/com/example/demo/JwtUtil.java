package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;
    
    // 토큰 유효 시간 (현재 10분으로 설정되어 있음)
    // 개발/테스트 중에는 너무 짧으면 불편할 수 있으니, 필요시 1000 * 60 * 60 (1시간) 등으로 늘려도 됨
    private final long expirationTime = 1000 * 60 * 10;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성 기능 (매개변수를 nickname에서 uid로 완전히 변경)
    public String generateToken(String uid) {
        return Jwts.builder()
                .setSubject(uid) // 이제 토큰의 메인 알맹이(Subject)로 uid가 들어갑니다.
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) 
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }

    /**
     * 2. 토큰에서 모든 정보(Claims)를 꺼내는 메서드
     * 서명 키를 이용해 토큰의 유효성을 검증하고 내부 데이터를 복호화합니다.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 발급 시 사용한 키로 검증 (위조 방지)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 3. 토큰에서 사용자 고유 식별자(uid)만 추출하는 메서드
     * 인터셉터에서 이 메서드를 호출하여 유저가 누구인지 식별합니다.
     */
    public String extractUid(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * 4. 토큰의 만료 여부를 확인하는 메서드 (보안 강화용)
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}