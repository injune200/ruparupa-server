package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 우리 서버의 모든 API 주소에 대하여
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // 프론트엔드 주소 허용 (프론트 팀원의 포트번호에 맞게 추가/수정)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 요청 방식
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}