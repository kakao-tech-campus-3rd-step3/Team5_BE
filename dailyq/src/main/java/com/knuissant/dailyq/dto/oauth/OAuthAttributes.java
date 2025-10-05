package com.knuissant.dailyq.dto.oauth;

import java.util.HashMap;
import java.util.Map;

import com.knuissant.dailyq.dto.users.UserCreateRequest;
import lombok.Getter;

import com.knuissant.dailyq.domain.users.User;
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
        // 수정 가능한 새 맵을 만들어 원본 속성을 복사합니다.
        Map<String, Object> modifiableAttributes = new HashMap<>(attributes);

        Object kakaoAccountObj = attributes.get("kakao_account");
        if (!(kakaoAccountObj instanceof Map<?,?> kakaoAccount)) {
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, "Invalid kakao account structure");
        }

        Object kakaoProfileObj = kakaoAccount.get("profile");
        if (!(kakaoProfileObj instanceof Map<?,?> kakaoProfile)) {
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, "Invalid kakao profile structure");
        }

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) kakaoProfile.get("nickname");

        // 최상위 속성으로 'email'과 'name'을 추가하여 데이터 구조를 통일합니다.
        modifiableAttributes.put("email", email);
        modifiableAttributes.put("name", nickname);

        return new OAuthAttributes(modifiableAttributes, userNameAttributeName, nickname, email);
    }

    /**
     * User 엔티티를 생성합니다.
     * @return User 엔티티
     */
    public User toEntity() {
        // OAuthAttributes에서 얻은 이메일과 이름으로 UserCreateRequest를 생성합니다.
        UserCreateRequest createRequest = new UserCreateRequest(this.email, this.name);
        // User의 정적 팩토리 메소드를 호출하여 User 엔티티를 생성합니다.
        return User.create(createRequest);
    }
}

