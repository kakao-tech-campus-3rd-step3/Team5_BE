package com.knuissant.dailyq.domain.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Job 도메인 테스트")
class JobTest {

    private Occupation occupation;
    private Job job;

    @BeforeEach
    void setUp() {
        occupation = Occupation.builder()
                .id(1L)
                .name("IT/개발")
                .build();

        job = Job.builder()
                .id(1L)
                .name("백엔드 개발자")
                .occupation(occupation)
                .build();
    }

    @Test
    @DisplayName("Job 생성 성공")
    void create_Success() {
        // when
        Job newJob = Job.create("프론트엔드 개발자", occupation);

        // then
        assertThat(newJob.getName()).isEqualTo("프론트엔드 개발자");
        assertThat(newJob.getOccupation()).isEqualTo(occupation);
    }

    @Test
    @DisplayName("Job 정보 수정 성공")
    void update_Success() {
        // given
        Occupation newOccupation = Occupation.builder()
                .id(2L)
                .name("디자인")
                .build();

        // when
        job.update("UI/UX 디자이너", newOccupation);

        // then
        assertThat(job.getName()).isEqualTo("UI/UX 디자이너");
        assertThat(job.getOccupation()).isEqualTo(newOccupation);
    }

    @Test
    @DisplayName("Job 이름만 수정 성공")
    void updateNameOnly_Success() {
        // when
        job.update("풀스택 개발자", occupation);

        // then
        assertThat(job.getName()).isEqualTo("풀스택 개발자");
        assertThat(job.getOccupation()).isEqualTo(occupation);
    }

    @Test
    @DisplayName("Job 직군만 수정 성공")
    void updateOccupationOnly_Success() {
        // given
        Occupation newOccupation = Occupation.builder()
                .id(2L)
                .name("경영/사무")
                .build();

        // when
        job.update("백엔드 개발자", newOccupation);

        // then
        assertThat(job.getName()).isEqualTo("백엔드 개발자");
        assertThat(job.getOccupation()).isEqualTo(newOccupation);
    }
}