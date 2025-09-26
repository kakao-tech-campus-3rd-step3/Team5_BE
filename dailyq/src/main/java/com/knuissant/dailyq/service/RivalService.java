package com.knuissant.dailyq.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.rivals.RivalListResponse;
import com.knuissant.dailyq.dto.rivals.RivalListResponse.CursorResult;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse.DailySolveCount;
import com.knuissant.dailyq.dto.rivals.RivalResponse;
import com.knuissant.dailyq.dto.rivals.RivalSearchResponse;
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

    @Transactional(readOnly = true)
    public RivalSearchResponse searchRivalByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return RivalSearchResponse.from(user);
    }

    @Transactional(readOnly = true)
    public RivalListResponse.CursorResult getFollowingRivalList(Long userId, Long lastId,
            int limit) {

        Pageable pageable = PageRequest.of(0, limit + 1, Sort.by("id").ascending());

        Slice<Rival> rivalsSlice;

        if (lastId == null) {
            rivalsSlice = rivalRepository.findAllBySenderId(userId, pageable);
        } else {
            rivalsSlice = rivalRepository.findBySenderIdAndIdGreaterThan(userId, lastId, pageable);
        }

        List<Rival> rivals = rivalsSlice.getContent();

        boolean hasNext = rivals.size() > limit;

        List<RivalListResponse> items = rivals.stream()
                .limit(limit)
                .map(rival -> RivalListResponse.from(rival.getReceiver()))
                .toList();

        Long nextCursor = hasNext ? items.getLast().userId() : null;

        return CursorResult.from(items, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public List<RivalListResponse> getFollowedRivalList(Long userId) {

        List<Rival> followedRivals = rivalRepository.findAllByReceiverId(userId);

        return followedRivals.stream()
                .map(rival -> RivalListResponse.from(rival.getSender()))
                .toList();
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }

}

