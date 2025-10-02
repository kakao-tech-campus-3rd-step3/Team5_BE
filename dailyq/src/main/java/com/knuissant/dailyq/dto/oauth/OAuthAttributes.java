package com.knuissant.dailyq.dto.oauth;

import java.util.Map;

import lombok.Getter;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserRole;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;

    // 생성자를 private으로 변경하여 외부에서의 직접 생성을 막고, 정적 팩토리 메소드를 통해서만 생성하도록 강제합니다.
    private OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    /**
     * 각 소셜 타입에 맞는 정적 팩토리 메소드를 호출하는 진입점 역할을 합니다.
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "kakao" -> ofKakao("id", attributes);
            default -> throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, "지원하지 않는 소셜 로그인입니다: " + registrationId);
        };
    }

    /**
     * Google 사용자 정보를 받아 OAuthAttributes 객체를 생성하는 정적 팩토리 메소드입니다.
     */
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(attributes, userNameAttributeName,
                (String) attributes.get("name"),
                (String) attributes.get("email"));
    }

    /**
     * Kakao 사용자 정보를 받아 OAuthAttributes 객체를 생성하는 정적 팩토리 메소드입니다.
     */
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuthAttributes(attributes, userNameAttributeName,
                (String) kakaoProfile.get("nickname"),
                (String) kakaoAccount.get("email"));
    }

    /**
     * User 엔티티를 생성합니다.
     * @return User 엔티티
     */
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .role(UserRole.FREE)
                .streak(0)
                .solvedToday(false)
                .build();
    }
}

