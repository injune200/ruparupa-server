package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, 
                      OAuth2SuccessHandler oAuth2SuccessHandler,
                      OAuth2FailureHandler oAuth2FailureHandler) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    this.oAuth2FailureHandler = oAuth2FailureHandler;
}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable()) // 폼 로그인 비활성화
            .httpBasic(basic -> basic.disable()) // 기본 HTTP 인증 비활성화
            // 세션을 사용하지 않도록 설정
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/currency/earn").permitAll() // POST 명시 추후 제거?
                .requestMatchers("/user/heartbeat", "/user/status").permitAll()
                .requestMatchers("/friends", "/friends/**").permitAll()
                .requestMatchers("/error").permitAll() // 디버깅용
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                // 로그인 성공 시 JWT를 생성해서 전달할 핸들러 등록
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler) 
            );

        return http.build();
    }
}