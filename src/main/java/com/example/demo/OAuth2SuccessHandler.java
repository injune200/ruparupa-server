package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                    Authentication authentication) throws IOException {
    
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        // JWT 생성[cite: 15]
        String token = jwtUtil.generateToken(nickname);

        // 앱으로 돌아가기 위한 딥링크 URL 생성 (ruparupa://auth)
        String targetUrl = UriComponentsBuilder.fromUriString("ruparupa://auth")
                .queryParam("accessToken", token)
                .queryParam("nickname", nickname)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        // 기존의 JSON 출력 코드를 지우고 아래 리다이렉트 코드를 사용합니다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}