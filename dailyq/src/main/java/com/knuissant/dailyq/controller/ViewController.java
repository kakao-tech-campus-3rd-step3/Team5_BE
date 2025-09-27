package com.knuissant.dailyq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 이 컨트롤러는 테스트용 View(HTML 페이지)를 보여주는 역할만 담당합니다.
 */
@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    // 루트 경로("/")로 접속 시 로그인 페이지로 이동시킵니다.
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
