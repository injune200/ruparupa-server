package com.example.demo;

import io.jsonwebtoken.Claims; // 추가됨
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
    private final long expirationTime = 1000 * 60 * 10;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성 기능 (기존과 동일)
    public String generateToken(String nickname) {
        return Jwts.builder()
                .setSubject(nickname) 
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) 
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }
    //토큰에서 모든 정보(Claims)를 꺼내는 메서드
    //서명 키를 이용해 토큰의 유효성을 검증하고 내부 데이터를 복합화합니다.
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 발급 시 사용한 키로 검증
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //토큰에서 사용자 닉네임(Subject)만 추출하는 메서드
    //CurrencyController에서 이 메서드를 호출하여 유저를 식별합니다.
    public String extractNickname(String token) {
        return extractAllClaims(token).getSubject();
    }


    //토큰의 만료 여부를 확인하는 메서드 (보안 강화용)
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}