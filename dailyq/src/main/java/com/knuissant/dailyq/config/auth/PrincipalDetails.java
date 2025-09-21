package com.knuissant.dailyq.config.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.knuissant.dailyq.domain.users.User;

import lombok.Getter;

@Getter
public class PrincipalDetails implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // --- OAuth2User 인터페이스 메서드 ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보를 반환합니다.
        return Collections.singletonList(() -> "ROLE_" + user.getRole().name());
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    // --- UserDetails 관련 메서드는 모두 제거. ---
}

