package com.knuissant.dailyq.config.oauth;

import java.util.Map;

import com.knuissant.dailyq.domain.users.User;

import lombok.Getter;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;

    private OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }

        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                (String)kakaoProfile.get("nickname"),
                (String)kakaoAccount.get("email")
        );
    }

    // 구글 응답을 파싱하는 ofGoogle 메서드를 추가합니다.
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                (String)attributes.get("name"),
                (String)attributes.get("email")
        );
    }

    public User toEntity() {
        return User.create(email, name);
    }
}

