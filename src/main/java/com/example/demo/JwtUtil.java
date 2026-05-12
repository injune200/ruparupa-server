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
    
    // 토큰 유효 시간 (1시간)
    private final long expirationTime = 1000 * 60 * 60;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성 기능 (uid를 사용하여 보안 강화)
    public String generateToken(String uid) {
        return Jwts.builder()
                .setSubject(uid) 
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) 
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }

    // 2. 토큰에서 모든 정보(Claims)를 꺼내는 메서드
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 3. 토큰에서 사용자 고유 식별자(uid)를 추출하는 메서드
    public String extractUid(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 4. 토큰의 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}