package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.users.User;
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

        User sender = findUserById(senderId);
        User receiver = findUserById(receiverId);

        validateRivalRequestNotExists(senderId, receiverId);

        Rival rivalRequest = Rival.create(sender, receiver);
        Rival savedRival = rivalRepository.save(rivalRequest);

        return RivalResponse.from(savedRival);
    }

    private User findUserById(Long userId) {
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
}
