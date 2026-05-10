package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 카카오 고유 ID로 기존 가입 여부 확인
    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByUid(String uid); // uid로 유저 찾기
}