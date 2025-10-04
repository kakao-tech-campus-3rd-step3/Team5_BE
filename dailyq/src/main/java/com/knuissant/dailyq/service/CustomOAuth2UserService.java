package com.knuissant.dailyq.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.oauth.OAuthAttributes;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

/**
 * Spring Security에서 OAuth2 로그인 성공 이후 후속 조치를 진행하는 사용자 정보 서비스 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;

    /**
     * 리소스 서버(구글, 카카오)에서 사용자 정보를 가져온 뒤 호출되는 메소드입니다.
     * @param userRequest 리소스 서버에서 넘어온 로그인 사용자 정보가 담긴 요청
     * @return 처리된 사용자 정보(OAuth2User) 객체. Spring Security의 인증 객체 생성에 사용됩니다.
     * @throws OAuth2AuthenticationException
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService 대리자를 생성하여 리소스 서버로부터 사용자 정보를 가져옴.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2. 소셜 로그인 서비스(예: "google", "naver")를 구분.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 3. OAuth2 로그인 진행 시 키가 되는 필드값(PK)을 가져옴.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. OAuthAttributes DTO를 사용하여 OAuth2에서 가져온 사용자 정보를 통일된 형식으로 변환.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 5. 획득한 속성을 기반으로 데이터베이스에 사용자 정보를 저장하거나 업데이트.
        User user = saveOrUpdate(attributes);

        // 6. 사용자의 권한과 속성을 포함하는 DefaultOAuth2User 객체를 생성하여 반환.
        //    이 객체는 Spring Security의 SecurityContext에 저장되어 로그인 상태를 유지하는 데 사용됩니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * OAuth 서비스로부터 얻은 정보로 신규 사용자를 저장하거나 기존 사용자의 정보를 업데이트하는 메소드입니다.
     * @param attributes 소셜 플랫폼에서 받아와 정리된 사용자 정보
     * @return 저장되거나 업데이트된 사용자 엔티티
     */
    private User saveOrUpdate(OAuthAttributes attributes) {
        if (!StringUtils.hasText(attributes.getEmail())) {
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, "Email not found from OAuth2 provider.");
        }
        
        // 이메일을 통해 데이터베이스에서 사용자를 찾음.
        Optional<User> existingUser = userRepository.findByEmail(attributes.getEmail());
        
        User user = existingUser
                // 만약 사용자가 이미 존재한다면, 이름(닉네임) 정보만 업데이트.
                .map(entity -> {
                    entity.updateName(attributes.getName());
                    return entity;
                })
                // 사용자가 존재하지 않는다면, OAuthAttributes의 toEntity() 메소드를 통해 새로운 사용자 엔티티를 생성.
                .orElse(attributes.toEntity());
        
        boolean isNewUser = existingUser.isEmpty();

        // 사용자 엔티티를 데이터베이스에 저장. (신규 사용자는 insert, 기존 사용자는 update)
        User savedUser = userRepository.save(user);
        
        // 신규 사용자인 경우 기본 UserPreferences 생성
        if (isNewUser) {
            try {
                userPreferencesService.createDefaultUserPreferences(savedUser.getId());
                log.info("기본 UserPreferences 생성 완료 - userId: {}", savedUser.getId());
            } catch (Exception e) {
                // UserPreferences 생성 실패는 로그인을 막지 않도록 로그만 남김
                log.warn("기본 UserPreferences 생성 실패 - userId: {}, error: {}", savedUser.getId(), e.getMessage(), e);
            }
        }
        
        return savedUser;
    }
    
}