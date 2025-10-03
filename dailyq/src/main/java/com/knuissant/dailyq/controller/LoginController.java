package com.knuissant.dailyq.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    /**
     * '/home' 경로로 GET 요청이 오면 이 메소드가 처리합니다.
     * 로그인한 사용자의 정보를 조회하여 home.html 뷰에 전달합니다.
     */
    @GetMapping("/home")
    public String home(Model model) {
        // SecurityContext에서 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            String userName = null;
            Object principal = authentication.getPrincipal();

            // 1. Principal의 타입을 확인하여 분기 처리합니다.
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                // --- JWT로 인증된 경우 ---
                org.springframework.security.core.userdetails.User jwtUser = (org.springframework.security.core.userdetails.User) principal;
                Long userId = Long.parseLong(jwtUser.getUsername());
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                userName = user.getName();

            } else if (principal instanceof OAuth2User) {
                // --- 소셜 로그인(OAuth2)으로 인증된 경우 ---
                OAuth2User oauth2User = (OAuth2User) principal;
                String email = extractEmail(oauth2User);
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                userName = user.getName();
            }

            // 2. 조회된 사용자 이름을 Model에 추가합니다.
            model.addAttribute("userName", userName);
        }

        return "home";
    }

    /**
     * OAuth2User 객체에서 이메일을 추출하는 헬퍼 메소드입니다.
     * (OAuth2AuthenticationSuccessHandler의 로직과 동일)
     */
    private String extractEmail(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
                email = (String) kakaoAccount.get("email");
            }
        }
        return email;
    }
}

