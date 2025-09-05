package com.knuissant.dailyq.archive.service;

import com.knuissant.dailyq.answer.Answer;
import com.knuissant.dailyq.archive.dto.ArchiveResponse;
import com.knuissant.dailyq.archive.repository.ArchiveRepository;
import com.knuissant.dailyq.user.User;
import com.knuissant.dailyq.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {

  private final ArchiveRepository archiveRepository;
  private final UserRepository userRepository;

  private User findUserById(Long userId) {
    return userRepository.findById(userId);
    // GlobalErrorHandler Merge 후 수정
    //  .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }

  public Page<ArchiveResponse.Summary> getArchiveList(Long userId, Pageable pageable) {
    User user = findUserById(userId);
    Page<Answer> answers = archiveRepository.findByUser(user, pageable);

    return answers.map(ArchiveResponse.Summary::from);
  }

  public ArchiveResponse.Detail getArchiveDetail(Long userId, Long answerId) {
    User user = findUserById(userId);

    Answer answer = archiveRepository.findByIdAndUser(answerId, user);
    // GlobalErrorHandler Merge 후 수정
    //    .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

    return ArchiveResponse.Detail.from(answer);
  }

}
