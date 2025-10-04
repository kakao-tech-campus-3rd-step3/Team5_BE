package com.knuissant.dailyq.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.rivals.RivalListResponse;
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
        List<Answer> answers = answerRepository.findByUserIdAndCreatedAtGreaterThanEqual(userId,
                forOneYear);

        Map<LocalDate, Long> countByDate = answers.stream()
                .collect(Collectors.groupingBy(
                        answer -> answer.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<DailySolveCount> dailySolveCounts = countByDate.entrySet().stream()
                //Map은 직접 stream()이 불가 -> entrySet()을 거치기
                .map(entry -> new DailySolveCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailySolveCount::date).reversed())
                .toList();

        return RivalProfileResponse.from(user, totalAnswerCount, dailySolveCounts);
    }

    @Transactional(readOnly = true)
    public RivalSearchResponse searchRivalByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return RivalSearchResponse.from(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public RivalListResponse.CursorResult getFollowingRivalList(Long userId, Long lastId,
            int limit) {

        Pageable pageable = PageRequest.of(0, limit + 1);

        Slice<Rival> rivalsSlice = (lastId == null)
                ? rivalRepository.findAllBySenderId(userId, pageable)
                : rivalRepository.findBySenderIdAndIdGreaterThan(userId, lastId, pageable);

        return createCursorResult(rivalsSlice, limit, true);
    }

    @Transactional(readOnly = true)
    public RivalListResponse.CursorResult getFollowedRivalList(Long userId, Long lastId,
            int limit) {

        Pageable pageable = PageRequest.of(0, limit + 1, Sort.by("id").ascending());

        Slice<Rival> rivalsSlice = (lastId == null)
                ? rivalRepository.findAllByReceiverId(userId, pageable)
                : rivalRepository.findByReceiverIdAndIdGreaterThan(userId, lastId, pageable);

        return createCursorResult(rivalsSlice, limit, false);
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }

    private RivalListResponse.CursorResult createCursorResult(Slice<Rival> rivalsSlice, int limit,
            boolean isFollowingList) {

        boolean hasNext = rivalsSlice.hasNext();

        List<RivalListResponse> items = rivalsSlice.getContent().stream()
                .limit(limit)
                .map(rival -> {
                    // boolean 플래그 값에 따라 sender를 가져올지 receiver를 가져올지 결정
                    User userToShow = isFollowingList ? rival.getReceiver() : rival.getSender();
                    return RivalListResponse.from(userToShow.getId(), userToShow.getName(),
                            userToShow.getEmail());
                })
                .toList();

        Long nextCursor = hasNext && !items.isEmpty()
                ? items.getLast().userId() : null;

        return new RivalListResponse.CursorResult(items, nextCursor, hasNext);
    }


}

