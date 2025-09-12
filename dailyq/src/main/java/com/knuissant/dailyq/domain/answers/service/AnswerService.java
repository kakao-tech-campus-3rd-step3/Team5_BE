package com.knuissant.dailyq.domain.answers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.answers.dto.request.AnswerUpdateRequest;
import com.knuissant.dailyq.domain.answers.dto.response.AnswerDetailResponse;
import com.knuissant.dailyq.domain.answers.dto.response.AnswerListResponse.CursorResult;
import com.knuissant.dailyq.domain.answers.dto.response.AnswerListResponse.Summary;
import com.knuissant.dailyq.domain.answers.dto.request.AnswerSearchConditionRequest;
import com.knuissant.dailyq.domain.answers.dto.response.AnswerUpdateResponse;
import com.knuissant.dailyq.domain.answers.repository.AnswerRepository;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    //private final FeedbackRepository feedbackRepository;
    //커서 생성/파싱
    private final ObjectMapper objectMapper;

    // 커서 내용 담을 DTO
    public record CursorRequest(LocalDateTime answeredTime, Long id) {

    }

    public CursorResult<Summary> getArchives(Long userId, AnswerSearchConditionRequest condition,
            String cursor, int limit) {

        //커서 파싱
        CursorRequest cursorRequest = parseCursor(cursor);

        String actualSortOrder = (condition.sortOrder() != null) ? condition.sortOrder() : "DESC";
        boolean isDesc = !"ASC".equalsIgnoreCase(actualSortOrder);

        // answeredTime + answerId를 통한 유니크 순서 보장
        Sort.Direction direction = isDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "answeredTime").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, limit + 1, sort);

        Specification<Answer> spec = createSpecification(userId, condition, cursorRequest, isDesc);

        List<Answer> answers = answerRepository.findAll(spec, pageable).getContent();
        return convertToCursorResult(answers, limit);
    }

    private Specification<Answer> createSpecification(Long userId, AnswerSearchConditionRequest condition, CursorRequest cursorRequest, boolean isDesc) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (cursorRequest != null) {
                Predicate timePredicate = isDesc ?
                        cb.lessThan(root.get("answeredTime"), cursorRequest.answeredTime()) :
                        cb.greaterThan(root.get("answeredTime"), cursorRequest.answeredTime());

                Predicate tieBreaker = cb.and(
                        cb.equal(root.get("answeredTime"), cursorRequest.answeredTime()),
                        isDesc ? cb.lessThan(root.get("id"), cursorRequest.id())
                                : cb.greaterThan(root.get("id"), cursorRequest.id())
                );
                predicates.add(cb.or(timePredicate, tieBreaker));
            }

            Join<Answer, Question> questionJoin = null;

            if (condition.date() != null) {
                predicates.add(cb.equal(root.get("answeredDate"), condition.date()));
            }
            if (condition.jobId() != null) {
                if (questionJoin == null) questionJoin = root.join("question", JoinType.INNER);
                Join<Question, Job> jobsJoin = questionJoin.join("jobs", JoinType.INNER);
                predicates.add(cb.equal(jobsJoin.get("id"), condition.jobId()));
            }
            if (condition.questionType() != null) {
                if (questionJoin == null) questionJoin = root.join("question", JoinType.INNER);
                predicates.add(cb.equal(questionJoin.get("questionType"), condition.questionType()));
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

        String nextCursor = hasNext ? createCursor(content.get(content.size() - 1)) : null;

        return new CursorResult<>(summaries, nextCursor, hasNext);
    }

    // 커서 생성/파싱 로직
    private String createCursor(Answer answer) {
        try {
            CursorRequest cursorData = new CursorRequest(answer.getAnsweredTime(), answer.getId());
            String json = objectMapper.writeValueAsString(cursorData);
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.CURSOR_GENERATION_FAILED);
        }
    }

    private CursorRequest parseCursor(String cursorStr) {
        if (cursorStr == null || cursorStr.isBlank()) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursorStr);
            return objectMapper.readValue(new String(decodedBytes), CursorRequest.class);
        } catch (IOException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_CURSOR);
        }
    }

    public AnswerDetailResponse getAnswerDetail(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));
        //Feedback feedback = feedbackRepository.findByAnswerId(answerId).orElse(null);
        Feedback feedback = null;
        return AnswerDetailResponse.of(answer, feedback);
    }

    @Transactional
    public AnswerUpdateResponse updateAnswer(Long userId, Long answerId, AnswerUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        // 인가
        if (!answer.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (request.memo() != null) {
            answer.updateMemo(request.memo());
        }

        if (request.starred() != null) {
            answer.updateStarred(request.starred());
        }

        if(request.level() != null) {
            answer.updateLevel(request.level());
        }

        return AnswerUpdateResponse.from(answer);
    }
}
