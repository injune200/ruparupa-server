package com.example.demo;

import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //부모 클래스의 기능을 이용해 기본 사용자 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        try {
            //카카오 고유 식별값 및 계정 정보 추출
            Long kakaoId = (Long) attributes.get("id");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            
            if (kakaoAccount == null) {
                throw new OAuth2AuthenticationException("카카오 계정 정보를 찾을 수 없습니다.");
            }
            
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (profile != null) ? (String) profile.get("nickname") : "Guest";

            //DB 저장 또는 업데이트 (JPA 활용)
            userRepository.findByKakaoId(kakaoId)
                .map(entity -> { // 이미 가입된 유저라면 닉네임 최신화
                    entity.setNickname(nickname);
                    return userRepository.save(entity);
                })
                .orElseGet(() -> { // 신규 유저라면 새로 저장 (회원가입)
                    return userRepository.save(new User(nickname, kakaoId));
                });

            //인증 객체 반환
            return oAuth2User;

        } catch (Exception e) {
            // 에러 발생 시 로그
            System.err.println("OAuth2 로그인 처리 에러: " + e.getMessage());
            throw new OAuth2AuthenticationException("사용자 정보 처리 중 오류 발생");
        }
    }
}