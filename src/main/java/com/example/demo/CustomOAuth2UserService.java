package com.example.demo;

import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 데이터베이스에 접근할 수 있는 Repository를 불러옵니다.
    private final UserRepository userRepository;

    // [생성자 주입] 스프링이 알아서 UserRepository를 연결해줍니다.
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //카카오 로그인 성공 후 사용자 정보를 불러오는 시점에 실행됩니다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 부모 클래스의 기능을 사용해 카카오로부터 사용자 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 카카오로부터 받은 전체 정보를 Map 형태로 꺼냅니다.
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 2. 카카오의 고유 식별 번호(ID)를 가져옵니다. (사용자 구분을 위한 고유 키)
        Long kakaoId = (Long) attributes.get("id"); 
    
        // 3. 계정 정보와 프로필 정보를 단계별로 파고들어 닉네임을 추출합니다.
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");
        
        // 4. [DB 저장 로직] 
        // findByKakaoId: 이미 가입된 사용자인지 DB에서 확인합니다.
        // orElseGet: 만약 없는 사용자라면(새로 저장합니다.
        userRepository.findByKakaoId(kakaoId)
            .orElseGet(() -> userRepository.save(new User(nickname, kakaoId)));

        System.out.println("로그인 및 닉네임 저장 완료: " + nickname);

        // 최종적으로 인증된 사용자 정보를 반환합니다.
        return oAuth2User;
    }
}