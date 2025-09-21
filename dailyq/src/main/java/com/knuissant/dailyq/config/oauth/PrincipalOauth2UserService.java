package com.knuissant.dailyq.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.knuissant.dailyq.config.auth.PrincipalDetails;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.service.UserPreferencesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("Kakao User Attributes : {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        if (!StringUtils.hasText(attributes.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider.");
        }

        User user = saveOrUpdate(attributes);

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    log.info("기존 회원입니다: {}", attributes.getEmail());
                    return entity;
                })
                .orElseGet(() -> {
                    log.info("신규 회원입니다. 자동 회원가입을 진행합니다: {}", attributes.getEmail());
                    User newUser = attributes.toEntity();
                    userRepository.save(newUser);
                    userPreferencesService.createDefaultPreferences(newUser);
                    return newUser;
                });
        return user;
    }
}

