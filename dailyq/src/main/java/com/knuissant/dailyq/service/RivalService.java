package com.knuissant.dailyq.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse.DailySolveCount;
import com.knuissant.dailyq.dto.rivals.RivalResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.RivalRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RivalService {

    private final RivalRepository rivalRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public RivalResponse followRival(Long senderId, Long receiverId) {

        if (senderId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.CANNOT_RIVAL_YOURSELF, senderId);
        }

        User sender = findUserByIdOrThrow(senderId);
        User receiver = findUserByIdOrThrow(receiverId);

        if (rivalRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {

            throw new BusinessException(ErrorCode.ALREADY_FOLLOWING_RIVAL, senderId,
                    receiverId);
        }

        Rival rivalShip = rivalRepository.save(Rival.create(sender, receiver));

        return RivalResponse.from(rivalShip);
    }

    public void unfollowRival(Long senderId, Long receiverId) {
        Rival rivalShip = rivalRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.RIVAL_RELATION_NOT_FOUND, senderId,
                                receiverId));
        rivalRepository.delete(rivalShip);
    }

    @Transactional(readOnly = true)
    public RivalProfileResponse getProfile(Long userId) {
        User user = findUserByIdOrThrow(userId);

        long totalAnswerCount = answerRepository.countByUserId(userId);

        LocalDateTime forOneYear = LocalDateTime.now().minusYears(1);
        List<DailySolveCount> dailySolveCounts = answerRepository.findDailySolveCountsByUserId(
                userId, forOneYear);

        return RivalProfileResponse.from(user, totalAnswerCount, dailySolveCounts);
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }

}

