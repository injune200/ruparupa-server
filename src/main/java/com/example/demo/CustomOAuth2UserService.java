package com.example.demo;

import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PetService petService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        try {
            // 카카오 고유 식별값 및 계정 정보 추출
            Long kakaoId = (Long) attributes.get("id");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            
            if (kakaoAccount == null) {
                throw new OAuth2AuthenticationException("카카오 계정 정보를 찾을 수 없습니다.");
            }
            
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (profile != null && profile.get("nickname") != null) 
                    ? (String) profile.get("nickname") 
                    : "새로운 유저";

            // DB에서 유저를 조회하고, 없으면 신규 유저 생성 및 초기 펫 세팅 진행
            userRepository.findByKakaoId(kakaoId)
                    .orElseGet(() -> {
                        // 1. 신규 유저 엔티티 생성 및 저장 (생성자 순서 맞춤)
                        User newUser = new User(nickname, kakaoId);
                        userRepository.save(newUser);

                        // 2. 초기 펫 및 방 데이터 자동 생성
                        petService.createInitialSetup(newUser);

                        return newUser;
                    });

            return new DefaultOAuth2User(
                    Collections.emptyList(),
                    attributes,
                    "id"
            );
        } catch (Exception e) {
            System.err.println("OAuth2 로그인 처리 에러: " + e.getMessage());
            throw new OAuth2AuthenticationException("사용자 정보 처리 중 오류 발생");
        }
    }
}