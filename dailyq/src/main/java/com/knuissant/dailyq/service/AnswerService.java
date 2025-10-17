package com.knuissant.dailyq.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.dto.answers.AnswerCreateResponse;
import com.knuissant.dailyq.dto.answers.AnswerDetailResponse;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerListResponse.CursorResult;
import com.knuissant.dailyq.dto.answers.AnswerListResponse.Summary;
import com.knuissant.dailyq.dto.answers.AnswerSearchConditionRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.FeedbackRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final FollowUpQuestionService followUpQuestionService;
    private final FeedbackService feedbackService;
    private final ObjectMapper objectMapper;

    //API 스펙과 무관하며(오로지,내부사용) 재사용 가능성이 없다고 생각하여 따로 DTO를 만들지 않았습니다.
    private record CursorRequest(LocalDateTime createdAt, Long id) {

    }

    private void checkAnswerOwnership(Long userId, Answer answer) {
        if (!answer.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    @Transactional(readOnly = true)
    public CursorResult<Summary> getArchives(Long userId, AnswerSearchConditionRequest condition,
            Long lastId, LocalDateTime lastCreatedAt, int limit) {

        CursorRequest cursorRequest = (lastId != null && lastCreatedAt != null)
                ? new CursorRequest(lastCreatedAt, lastId)
                : null;

        String actualSortOrder = (condition.sortOrder() != null) ? condition.sortOrder() : "DESC";
        boolean isDesc = !"ASC".equalsIgnoreCase(actualSortOrder);

        // createdAt + answerId를 통한 유니크 순서 보장
        Sort.Direction direction = isDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, limit + 1, sort);

        Specification<Answer> spec = createSpecification(userId, condition, cursorRequest, isDesc);

        Slice<Answer> slice = answerRepository.findAll(spec, pageable);

        return convertToCursorResult(slice.getContent(), limit);
    }

    @Transactional
    public AnswerArchiveUpdateResponse updateAnswer(Long userId, Long answerId,
            AnswerArchiveUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, answerId));
        // 인가
        checkAnswerOwnership(userId, answer);

        if (request.memo() != null) {
            answer.updateMemo(request.memo());
        }

        if (request.starred() != null) {
            answer.updateStarred(request.starred());
        }

        if (request.level() != null) {
            answer.updateLevel(request.level());
        }

        return AnswerArchiveUpdateResponse.from(answer);
    }

    @Transactional(readOnly = true)
    public AnswerDetailResponse getAnswerDetail(Long userId, Long answerId) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, answerId));

        checkAnswerOwnership(userId, answer);

        Feedback feedback = feedbackRepository.findByAnswerId(answerId).orElse(null);
        return AnswerDetailResponse.of(answer, feedback, objectMapper);

    }

    @Transactional
    public AnswerCreateResponse submitAnswer(Long userId, AnswerCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Answer savedAnswer = isFollowUpQuestion(request.questionId())
                ? handleFollowUpQuestionAnswer(request, user)
                : handleRegularQuestionAnswer(request, user);

        Feedback savedFeedback = feedbackService.createPendingFeedback(savedAnswer);

        return AnswerCreateResponse.from(savedAnswer, savedFeedback);
    }

    private boolean isFollowUpQuestion(Long questionId) {
        return questionId < 0;
    }

    private Answer handleFollowUpQuestionAnswer(AnswerCreateRequest request, User user) {
        Long followUpQuestionId = Math.abs(request.questionId());
        FollowUpQuestion followUpQuestion = followUpQuestionService.getFollowUpQuestion(followUpQuestionId);
        Question question = followUpQuestion.getAnswer().getQuestion();

        // 추후 audioUrl -> answerText로 반환 후 저장 로직 추가
        Answer answer = Answer.create(user, question, request.answerText());
        answer.setFollowUpQuestion(followUpQuestion);

        Answer savedAnswer = answerRepository.save(answer);
        followUpQuestionService.markFollowUpQuestionAsAnswered(followUpQuestionId);

        return savedAnswer;
    }

    private Answer handleRegularQuestionAnswer(AnswerCreateRequest request, User user) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, request.questionId()));

        // 추후 audioUrl -> answerText로 반환 후 저장 로직 추가
        Answer answer = Answer.create(user, question, request.answerText());
        return answerRepository.save(answer);
    }

    @Transactional
    public AnswerLevelUpdateResponse updateAnswerLevel(Long userId, Long answerId,
            AnswerLevelUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, answerId));

        checkAnswerOwnership(userId, answer);

        answer.updateLevel(request.level());
        return AnswerLevelUpdateResponse.from(answer);
    }

    private Specification<Answer> createSpecification(Long userId,
            AnswerSearchConditionRequest condition, AnswerService.CursorRequest cursorRequest,
            boolean isDesc) {
        return (root, query, cb) -> {

            assert query != null;
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("question", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (cursorRequest != null) {
                Predicate timePredicate = isDesc ?
                        cb.lessThan(root.get("createdAt"), cursorRequest.createdAt()) :
                        cb.greaterThan(root.get("createdAt"), cursorRequest.createdAt());

                Predicate tieBreaker = cb.and(
                        cb.equal(root.get("createdAt"), cursorRequest.createdAt()),
                        isDesc ? cb.lessThan(root.get("id"), cursorRequest.id())
                                : cb.greaterThan(root.get("id"), cursorRequest.id())
                );
                predicates.add(cb.or(timePredicate, tieBreaker));
            }

            Join<Answer, Question> questionJoin = null;

            if (condition.date() != null) {
                LocalDateTime startOfDay = condition.date().atStartOfDay();
                LocalDateTime endOfDay = condition.date().atTime(LocalTime.MAX);

                predicates.add(cb.between(root.get("createdAt"), startOfDay, endOfDay));
            }
            if (condition.jobId() != null) {
                if (questionJoin == null) {
                    questionJoin = root.join("question", JoinType.INNER);
                }
                Join<Question, Job> jobsJoin = questionJoin.join("jobs", JoinType.INNER);
                predicates.add(cb.equal(jobsJoin.get("id"), condition.jobId()));
            }
            if (condition.questionType() != null) {
                if (questionJoin == null) {
                    questionJoin = root.join("question", JoinType.INNER);
                }
                predicates.add(
                        cb.equal(questionJoin.get("questionType"), condition.questionType()));
            }
            if (condition.starred() != null) {
                predicates.add(cb.equal(root.get("starred"), condition.starred()));
            }
            if (condition.level() != null) {
                predicates.add(cb.equal(root.get("level"), condition.level()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CursorResult<Summary> convertToCursorResult(List<Answer> answers, int limit) {
        boolean hasNext = answers.size() > limit;
        List<Answer> content = hasNext ? answers.subList(0, limit) : answers;

        List<Summary> summaries = content.stream()
                .map(Summary::from)
                .toList();

        Long nextId = null;
        LocalDateTime nextCreatedAt = null;

        if (hasNext) {
            Answer lastAnswer = content.get(content.size() - 1);
            nextId = lastAnswer.getId();
            nextCreatedAt = lastAnswer.getCreatedAt();
        }

        return new CursorResult<>(summaries, nextId, nextCreatedAt, hasNext);
    }
}
