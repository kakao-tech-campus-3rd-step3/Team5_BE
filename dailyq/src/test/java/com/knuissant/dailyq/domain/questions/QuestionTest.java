package com.knuissant.dailyq.domain.questions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.jobs.Occupation;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Question 도메인 테스트")
class QuestionTest {

    private Question question;
    private Job job1;
    private Job job2;
    private Set<Job> jobs;

    @BeforeEach
    void setUp() {
        Occupation occupation = Occupation.builder()
                .id(1L)
                .name("IT/개발")
                .build();

        job1 = Job.builder()
                .id(1L)
                .name("백엔드 개발자")
                .occupation(occupation)
                .build();

        job2 = Job.builder()
                .id(2L)
                .name("프론트엔드 개발자")
                .occupation(occupation)
                .build();

        jobs = new HashSet<>(Arrays.asList(job1));

        question = Question.builder()
                .id(1L)
                .questionText("REST API의 장단점은 무엇인가요?")
                .questionType(QuestionType.TECH)
                .enabled(true)
                .jobs(jobs)
                .build();
    }

    @Test
    @DisplayName("Question 생성 성공")
    void create_Success() {
        // when
        Question newQuestion = Question.create(
                "새로운 질문",
                QuestionType.PERSONALITY,
                new HashSet<>(Arrays.asList(job1, job2))
        );

        // then
        assertThat(newQuestion.getQuestionText()).isEqualTo("새로운 질문");
        assertThat(newQuestion.getQuestionType()).isEqualTo(QuestionType.PERSONALITY);
        assertThat(newQuestion.getEnabled()).isTrue();
        assertThat(newQuestion.getJobs()).hasSize(2);
    }

    @Test
    @DisplayName("Question 전체 정보 수정 성공")
    void update_Success() {
        // given
        Set<Job> newJobs = new HashSet<>(Arrays.asList(job1, job2));

        // when
        question.update(
                "수정된 질문",
                QuestionType.PERSONALITY,
                false,
                newJobs
        );

        // then
        assertThat(question.getQuestionText()).isEqualTo("수정된 질문");
        assertThat(question.getQuestionType()).isEqualTo(QuestionType.PERSONALITY);
        assertThat(question.getEnabled()).isFalse();
        assertThat(question.getJobs()).hasSize(2);
    }

    @Test
    @DisplayName("Question 텍스트만 수정")
    void updateTextOnly_Success() {
        // when
        question.update(
                "텍스트만 수정",
                question.getQuestionType(),
                question.getEnabled(),
                question.getJobs()
        );

        // then
        assertThat(question.getQuestionText()).isEqualTo("텍스트만 수정");
        assertThat(question.getQuestionType()).isEqualTo(QuestionType.TECH);
        assertThat(question.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("Question enabled 상태만 수정")
    void updateEnabledOnly_Success() {
        // when
        question.update(
                question.getQuestionText(),
                question.getQuestionType(),
                false,
                question.getJobs()
        );

        // then
        assertThat(question.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("Question 타입만 수정")
    void updateTypeOnly_Success() {
        // when
        question.update(
                question.getQuestionText(),
                QuestionType.PERSONALITY,
                question.getEnabled(),
                question.getJobs()
        );

        // then
        assertThat(question.getQuestionType()).isEqualTo(QuestionType.PERSONALITY);
    }

    @Test
    @DisplayName("Question 연관 직업 목록만 수정")
    void updateJobsOnly_Success() {
        // given
        Set<Job> newJobs = new HashSet<>(Arrays.asList(job1, job2));

        // when
        question.update(
                question.getQuestionText(),
                question.getQuestionType(),
                question.getEnabled(),
                newJobs
        );

        // then
        assertThat(question.getJobs()).hasSize(2);
        assertThat(question.getJobs()).contains(job1, job2);
    }
}