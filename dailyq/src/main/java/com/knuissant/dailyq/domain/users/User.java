package com.knuissant.dailyq.domain.users;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.knuissant.dailyq.domain.common.BaseTimeEntity;
import com.knuissant.dailyq.dto.users.UserCreateRequest;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    private static final int MAX_NAME_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    @NotNull(message = "역할은 필수입니다.")
    private UserRole role;

    @Column(nullable = false)
    private Integer streak;

    @Column(name = "solved_today", nullable = false)
    private Boolean solvedToday;

    @Column(name = "last_solved_date")
    private LocalDate lastSolvedDate;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    public static User create(UserCreateRequest request) {
        return User.builder()
                .email(request.email())
                .name(request.name())
                .role(UserRole.FREE)
                .streak(0)
                .solvedToday(false)
                .build();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 " + MAX_NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    public void updateRole(UserRole role) {
        this.role = role;
    }

    public void updateStreakOnActivity() {
        LocalDate today = LocalDate.now();

        if (this.lastSolvedDate == null) {
            return;
        }

        if (this.lastSolvedDate.equals(today) || this.lastSolvedDate.equals(today.minusDays(1))) {
            if (Boolean.TRUE.equals(this.solvedToday) && !this.lastSolvedDate.equals(today)) {
                this.solvedToday = false;
            }
            return;
        }

        this.streak = 0;
        this.solvedToday = false;
    }


    public void markAsSolvedToday() {
        LocalDate today = LocalDate.now();

        if (Boolean.TRUE.equals(this.solvedToday) && today.equals(this.lastSolvedDate)) {
            return;
        }

        this.solvedToday = true;
        this.lastSolvedDate = today;
    }
}
