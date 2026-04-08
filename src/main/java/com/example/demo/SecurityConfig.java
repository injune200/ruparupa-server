package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // 추가 필요
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // 추가 필요
import org.springframework.security.web.SecurityFilterChain; // 추가 필요

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 테스트를 위해 CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**").permitAll() // 로그인 관련 페이지는 누구나 접근 가능
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 핵심 로직 클래스 등록
                )
                .defaultSuccessUrl("/home", true) // 로그인 성공 후 이동할 페이지
            );

        return http.build();
    }
}