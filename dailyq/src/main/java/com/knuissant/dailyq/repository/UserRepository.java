package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 해결 여부와 생성 시간으로 사용자 조회
    Optional<User> findBySolvedTodayAndCreatedAtAfter(Boolean solvedToday, LocalDateTime createdAt);
}