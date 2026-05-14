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
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException {
    
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 1. 카카오 최상위 속성에서 고유 ID를 추출합니다.
        Long kakaoId = (Long) attributes.get("id");

        // 2. 카카오 고유 ID로 유저를 안전하게 조회합니다.
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
        
        // 3. 토큰 발급
        String token = jwtUtil.generateToken(user.getUid());
        

        // 4. [중요] 프론트엔드에서 요구한 데이터를 모두 파라미터로 담아 딥링크 URL을 생성합니다.
        String targetUrl = UriComponentsBuilder.fromUriString("ruparupa://auth")
                .queryParam("accessToken", token)
                .queryParam("userId", user.getUid()) // JSON 명세에 맞춰 uid 대신 userId로 전달
                .queryParam("nickname", user.getNickname())
                .queryParam("friendCode", user.getFriendCode()) // 추가: 친구 코드
                .queryParam("gold", user.getGold()) // 추가: 보유 골드
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        // 5. 확인을 위한 서버 콘솔 로그 출력 (생성된 최종 URL 확인용)
        System.out.println("=================================================");

        System.out.println("발급된 테스트용 토큰: Bearer " + token);

        System.out.println("=================================================");
        System.out.println("=================================================");
        System.out.println("로그인 성공! 유저 닉네임: " + user.getNickname());
        System.out.println("프론트엔드로 전달할 딥링크 주소:");
        System.out.println(targetUrl);
        System.out.println("=================================================");

        // 6. 앱(프론트엔드)으로 리다이렉트 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}