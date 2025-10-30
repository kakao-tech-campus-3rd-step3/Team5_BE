package com.knuissant.dailyq.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.rivals.Rival;

public interface RivalRepository extends JpaRepository<Rival, Long> {
    Optional<Rival> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @EntityGraph(attributePaths = {"sender","receiver"})
    Slice<Rival> findBySenderIdAndIdGreaterThanOrderByIdAsc(Long senderId, Long lastId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender","receiver"})
    Slice<Rival> findByReceiverIdAndIdGreaterThanOrderByIdAsc(Long receiverId, Long lastId, Pageable pageable);

    // 첫 페이지 조회
    Slice<Rival> findAllBySenderId(Long senderId, Pageable pageable);

    Slice<Rival> findAllByReceiverId(Long receiverId, Pageable pageable);

}
