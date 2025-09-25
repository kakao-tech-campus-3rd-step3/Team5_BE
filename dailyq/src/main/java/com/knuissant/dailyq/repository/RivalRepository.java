package com.knuissant.dailyq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knuissant.dailyq.domain.rivals.Rival;

public interface RivalRepository extends JpaRepository<Rival, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Rival> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Rival> findAllBySenderId(Long senderId);

}
