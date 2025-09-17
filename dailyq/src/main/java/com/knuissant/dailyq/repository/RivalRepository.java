package com.knuissant.dailyq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.rivals.RivalStatus;

public interface RivalRepository extends JpaRepository<Rival, Long> {

    // WAITING 상태인 즉 사용자가 받은 라이벌 신청 목록 조회
    List<Rival> findByReceiverIdAndStatus(Long receiverId, RivalStatus status);

    // 사용자간 라이벌 관계 조회
    Optional<Rival> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

}
