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
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(
            new JavaTimeModule());

    // 커서 내용 담을 DTO
    public record CursorRequest(LocalDateTime answeredTime, Long id) {

    }

    public CursorResult<Summary> getArchives(Long userId, AnswerSearchConditionRequest condition,
            String cursor, int limit) {

        //커서 파싱
        CursorRequest cursorRequest = parseCursor(cursor);

        // answeredTime + answerId를 통한 유니크 순서 보장
        boolean isDesc = !"ASC".equalsIgnoreCase(condition.getSortOrder());
        Sort.Direction direction = isDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "answeredTime").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, limit + 1, sort);

        //Predicate : SQL의 WHERE 조건을 나타내는 객체
        // root: 테이블의 별칭 역할 (ex FROM ANSWER a에서 a)
        // query: 기준 쿼리 객체 (SELECT, GROUP BY)
        // cb: 기준 빌더 (WHERE 조건식 생성 AND, OR)
        Specification<Answer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (cursorRequest != null) {
                //우선 답변 시간 기준 정렬
                Predicate timePredicate = isDesc ?
                        cb.lessThan(root.get("answeredTime"), cursorRequest.answeredTime()) :
                        cb.greaterThan(root.get("answeredTime"), cursorRequest.answeredTime());

                //동일 답변 시간일 때 answerId로 구분
                Predicate tieBreaker = cb.and(
                        cb.equal(root.get("answeredTime"), cursorRequest.answeredTime()),
                        isDesc ? cb.lessThan(root.get("id"), cursorRequest.id())
                                : cb.greaterThan(root.get("id"), cursorRequest.id())
                );

                predicates.add(cb.or(timePredicate, tieBreaker));
            }
            // question 테이블 조인 중복 문제 해결
            Join<Answer, Question> questionJoin = null;

            // 필터 조건 : 날짜
            if (condition.getDate() != null) {
                predicates.add(cb.equal(root.get("answeredDate"), condition.getDate()));
            }

            // 필터 조건 : 직군 Id
            if (condition.getJobId() != null) {
                if (questionJoin == null) {
                    questionJoin = root.join("question", JoinType.INNER);
                }
                Join<Question, Job> jobsJoin = questionJoin.join("jobs", JoinType.INNER);
                predicates.add(cb.equal(jobsJoin.get("id"), condition.getJobId()));
            }

            // 필터 조건 : 질문 유형
            if (condition.getQuestionType() != null) {
                if (questionJoin == null) {
                    questionJoin = root.join("question", JoinType.INNER);
                }
                predicates.add(
                        cb.equal(questionJoin.get("questionType"), condition.getQuestionType()));
            }

            // 필터 조건 : 즐겨찾기 여부
            if (condition.getStarred() != null) {
                predicates.add(cb.equal(root.get("starred"), condition.getStarred()));
            }

            // 필터 조건 : 난이도
            if (condition.getLevel() != null) {
                predicates.add(cb.equal(root.get("level"), condition.getLevel()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Answer> answers = answerRepository.findAll(spec, pageable).getContent();
        return convertToCursorResult(answers, limit);
    }

    private CursorResult<Summary> convertToCursorResult(List<Answer> answers, int limit) {
        boolean hasNext = answers.size() > limit;
        List<Answer> content = hasNext ? answers.subList(0, limit) : answers;

        List<Summary> summaries = content.stream()
                .map(this::convertToSummary)
                .toList();

        String nextCursor = hasNext ? createCursor(content.get(content.size() - 1)) : null;

        return new CursorResult<>(summaries, nextCursor, hasNext);
    }

    private Summary convertToSummary(Answer answer) {
        return new Summary(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getQuestion().getQuestionText(),
                answer.getQuestion().getQuestionType(),
                null, // flow_phase는 보류
                answer.getLevel(),
                answer.getStarred(),
                answer.getAnsweredTime()
        );
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
    public void updateAnswer(Long userId, Long answerId, AnswerUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        // 인가
        if (!answer.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 2. DTO에 들어온 값이 null이 아닌 경우에만 엔티티의 값을 수정
        if (request.getMemo() != null) {
            answer.updateMemo(request.getMemo()); // (Answer 엔티티에 updateMemo 메서드 필요)
        }

        if (request.getStarred() != null) {
            answer.updateStarred(request.getStarred()); // (Answer 엔티티에 updateStarred 메서드 필요)
        }
    }
}
