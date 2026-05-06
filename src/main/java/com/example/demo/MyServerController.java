package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RestController;
@RestController 
public class MyServerController {

    @GetMapping("/home")
    public String home() {
        return "카카오 로그인에 성공했습니다! 홈 화면입니다.";
    }

    @GetMapping("/a")
    public String pageA() {
        return "A 페이지에 오신 걸 환영합니다!";
    }
}