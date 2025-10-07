package com.knuissant.dailyq.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.jwt.TokenProvider;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

@Slf4j
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    private long accessTokenExpirationMillis = 2592000000L;
    
    @Value("${dev.api.password}")
    private String devApiPassword;

    @GetMapping("/token")
    public ResponseEntity<Map<String, Object>> getDevToken(@RequestParam String password) {
        // 비밀번호 검증
        if (password == null || !devApiPassword.equals(password)) {
            log.warn("개발용 토큰 발급 API에 잘못된 비밀번호로 접근 시도");
            return ResponseEntity.status(401).body(Map.of(
                "error", "Unauthorized",
                "message", "Invalid password"
            ));
        }

        try {
            // Mock 데이터에 추가된 개발용 관리자 계정 조회
            User adminUser = userRepository.findByEmail("admin@dailyq.dev")
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "개발용 관리자 계정을 찾을 수 없습니다."));
            
            // 액세스 토큰 생성
            String accessToken = tokenProvider.generateAccessToken(adminUser);
            
            log.info("개발용 토큰 발급 완료 - userId: {}, email: {}", adminUser.getId(), adminUser.getEmail());
            
            // 토큰 응답 생성
            Map<String, Object> response = Map.of(
                "access_token", accessToken,
                "token_type", "Bearer",
                "expires_in", accessTokenExpirationMillis / 1000, // 초 단위로 변환
                "user_info", Map.of(
                    "userId", adminUser.getId(),
                    "email", adminUser.getEmail(),
                    "name", adminUser.getName(),
                    "role", adminUser.getRole().name()
                )
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("개발용 토큰 발급 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "토큰 발급에 실패했습니다.");
        }
    }
}
