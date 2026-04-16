package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper; // JSON 변환기 추가
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 자바 객체를 JSON으로 바꿔주는 도구

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

    // 1. JWT 생성
    String token = jwtUtil.generateToken(nickname);

    // 2. 프론트엔드에 넘겨줄 데이터 꾸러미 만들기
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("status", "success");
    responseData.put("message", "로그인 성공! 환영합니다.");
    responseData.put("accessToken", token);
    responseData.put("nickname", nickname);

    // 3. HTTP 응답 설정
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);

    // 4. Map을 JSON 문자열로 변환하여 출력
    String jsonResponse = objectMapper.writeValueAsString(responseData);
    response.getWriter().write(jsonResponse);
}
}