package com.knuissant.dailyq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.rivals.Rival;

public interface RivalRepository extends JpaRepository<Rival, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Rival> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Rival> findAllBySenderId(Long senderId);

    List<Rival> findAllByReceiverId(Long receiverId);

    @Query("SELECT r FROM Rival r WHERE r.sender.id = :senderId AND r.id > :lastId ORDER BY r.id ASC")
    Slice<Rival> findBySenderIdAndIdGreaterThan(@Param("senderId") Long senderId,
            @Param("lastId") Long lastId, Pageable pageable);

    // 첫 페이지 조회
    Slice<Rival> findAllBySenderId(Long senderId, Pageable pageable);
}
