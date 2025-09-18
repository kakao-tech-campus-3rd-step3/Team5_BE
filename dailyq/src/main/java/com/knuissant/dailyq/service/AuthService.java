package com.knuissant.dailyq.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.config.jwt.JwtProvider;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.auth.LoginRequestDto;
import com.knuissant.dailyq.dto.auth.TokenResponseDto;
import com.knuissant.dailyq.dto.users.UserCreateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public void signup(UserCreateRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        });
        // 1. 비밀번호를 암호화합니다.
        String encodedPassword = passwordEncoder.encode(request.password());
        // 2. User 엔티티의 팩토리 메서드를 사용하여 객체를 생성합니다.
        User newUser = User.create(request, encodedPassword);
        userRepository.save(newUser);
        // 3. 신규 회원의 기본 환경설정을 생성합니다.
        userPreferencesService.createDefaultPreferences(newUser);
    }

    public TokenResponseDto login(LoginRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // AuthenticationManager를 사용하여 인증을 수행합니다.
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = jwtProvider.createAccessToken(authentication);
        String refreshToken = jwtProvider.createRefreshToken(authentication);

        return new TokenResponseDto(accessToken, refreshToken);
    }
}