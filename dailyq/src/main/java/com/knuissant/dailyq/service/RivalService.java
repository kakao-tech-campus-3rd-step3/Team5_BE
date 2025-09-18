package com.knuissant.dailyq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.rivals.RivalStatus;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.rivals.ReceivedRivalRequest;
import com.knuissant.dailyq.dto.rivals.RivalResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.RivalRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RivalService {

    private final RivalRepository rivalRepository;
    private final UserRepository userRepository;

    public RivalResponse sendRivalRequest(Long senderId, Long receiverId) {

        User sender = findUserByIdOrThrow(senderId);
        User receiver = findUserByIdOrThrow(receiverId);

        validateRivalRequestNotExists(senderId, receiverId);

        Rival rivalRequest = Rival.create(sender, receiver);
        Rival savedRival = rivalRepository.save(rivalRequest);

        return RivalResponse.from(savedRival);
    }

    @Transactional(readOnly = true)
    public List<ReceivedRivalRequest> getReceivedRequests(Long receiverId) {

        if (!userRepository.existsById(receiverId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, receiverId);
        }

        return rivalRepository.findByReceiverIdAndStatus(receiverId, RivalStatus.WAITING)
                .stream()
                .map(ReceivedRivalRequest::from)
                .collect(Collectors.toList());
    }

    public RivalResponse acceptRivalRequest(Long senderId, Long receiverId) {

        Rival rivalRequest = findWaitingRivalRequest(senderId, receiverId);

        rivalRequest.accept(); //단방향

        return RivalResponse.from(rivalRequest);
    }

    public void rejectRivalRequest(Long senderId, Long receiverId) {

        Rival rivalRequest = findWaitingRivalRequest(senderId, receiverId);

        rivalRepository.delete(rivalRequest);
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }

    private void validateRivalRequestNotExists(Long senderId, Long receiverId) {
        boolean exists = rivalRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                rivalRepository.existsBySenderIdAndReceiverId(receiverId, senderId);

        if (exists) {
            throw new BusinessException(ErrorCode.RIVAL_REQUEST_ALREADY_EXIST, senderId,
                    receiverId);
        }
    }

    private Rival findWaitingRivalRequest(Long senderId, Long receiverId) {
        return rivalRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .filter(r -> r.getStatus() == RivalStatus.WAITING)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.RIVAL_REQUEST_NOT_FOUND, senderId,
                                receiverId));
    }
}
