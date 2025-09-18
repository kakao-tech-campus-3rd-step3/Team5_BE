package com.knuissant.dailyq.config.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.users.UserCreateRequest;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("기존 회원입니다: {}", email);
        } else {
            log.info("신규 회원입니다. 자동 회원가입을 진행합니다: {}", email);

            String tempPassword = UUID.randomUUID().toString();
            UserCreateRequest createRequest = new UserCreateRequest(email, tempPassword, name);

            User newUser = User.create(createRequest, passwordEncoder.encode(tempPassword));
            user = userRepository.save(newUser);
            userPreferencesService.createDefaultPreferences(user);
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}

