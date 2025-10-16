package com.knuissant.dailyq.domain.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 도메인 테스트")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .role(UserRole.FREE)
                .streak(5)
                .solvedToday(false)
                .build();
    }

    @Test
    @DisplayName("User 이름 수정 성공")
    void updateName_Success() {
        // when
        user.updateName("Updated Name");

        // then
        assertThat(user.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("User 이름을 빈 문자열로 수정 시 예외 발생")
    void updateName_EmptyString_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> user.updateName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름은 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("User 이름을 null로 수정 시 예외 발생")
    void updateName_Null_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> user.updateName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름은 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("User 이름을 100자 초과로 수정 시 예외 발생")
    void updateName_TooLong_ThrowsException() {
        // given
        String longName = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> user.updateName(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100자를 초과할 수 없습니다");
    }

    @Test
    @DisplayName("User 이름을 정확히 100자로 수정 성공")
    void updateName_ExactlyMaxLength_Success() {
        // given
        String maxLengthName = "a".repeat(100);

        // when
        user.updateName(maxLengthName);

        // then
        assertThat(user.getName()).isEqualTo(maxLengthName);
        assertThat(user.getName().length()).isEqualTo(100);
    }

    @Test
    @DisplayName("User 역할 수정 성공")
    void updateRole_Success() {
        // when
        user.updateRole(UserRole.PREMIUM);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.PREMIUM);
    }

    @Test
    @DisplayName("User 역할을 ADMIN으로 수정 성공")
    void updateRole_ToAdmin_Success() {
        // when
        user.updateRole(UserRole.ADMIN);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("User 여러 정보를 순차적으로 수정 성공")
    void updateMultipleFields_Success() {
        // when
        user.updateName("New Name");
        user.updateRole(UserRole.PREMIUM);
        user.updateRefreshToken("new-refresh-token");

        // then
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getRole()).isEqualTo(UserRole.PREMIUM);
        assertThat(user.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    @DisplayName("User RefreshToken 수정 성공")
    void updateRefreshToken_Success() {
        // when
        user.updateRefreshToken("new-token-12345");

        // then
        assertThat(user.getRefreshToken()).isEqualTo("new-token-12345");
    }
}