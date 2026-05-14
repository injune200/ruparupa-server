package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                // 1. 기본적으로 모든 API 경로에 문지기를 세웁니다.
                .addPathPatterns("/**") 
                // 2. 단, 카카오 로그인이나 에러 페이지 등은 토큰 검사를 하면 안 되므로 예외 처리합니다.
                .excludePathPatterns(
                        "/oauth2/**",
                        "/login/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}