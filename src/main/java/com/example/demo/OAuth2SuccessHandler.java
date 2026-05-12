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
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        // DB에서 유저 정보를 가져와서 고유 UID를 꺼냅니다.
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
        
        String token = jwtUtil.generateToken(user.getUid());

        // 팀원들을 위한 콘솔 출력 유지
        System.out.println("=================================================");
        System.out.println("로그인 성공! 유저 닉네임: " + nickname);
        System.out.println("발급된 테스트용 토큰: Bearer " + token);
        System.out.println("=================================================");

        String targetUrl = UriComponentsBuilder.fromUriString("ruparupa://auth")
                .queryParam("accessToken", token)
                .queryParam("nickname", nickname)
                .queryParam("uid", user.getUid())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}