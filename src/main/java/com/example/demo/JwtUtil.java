package com.example.demo;

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

    // application.yml의 jwt.secret 값을 가져와서 secretKey 변수에 넣습니다.
    @Value("${jwt.secret}")
    private String secretKey;

    // 실제로 암호화에 사용될 Key 객체입니다.
    private Key key;
    
    // 토큰 유효 시간 (1시간)
    private final long expirationTime = 1000 * 60 * 10;

    // 객체가 생성된 후(PostConstruct), 문자열을 실제 Key 객체로 변환합니다.
    @PostConstruct
    public void init() {
        // 문자열을 바이트 배열로 변환하여 HMAC-SHA 키로 만듭니다.
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String nickname) {
        return Jwts.builder()
                .setSubject(nickname) // 유저 닉네임 저장
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 3. 지정된 키와 알고리즘으로 서명
                .compact();
    }
}