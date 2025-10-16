package com.knuissant.dailyq.domain.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Occupation 도메인 테스트")
class OccupationTest {

    private Occupation occupation;

    @BeforeEach
    void setUp() {
        occupation = Occupation.builder()
                .id(1L)
                .name("IT/개발")
                .build();
    }

    @Test
    @DisplayName("Occupation 생성 성공")
    void create_Success() {
        // when
        Occupation newOccupation = Occupation.create("디자인");

        // then
        assertThat(newOccupation.getName()).isEqualTo("디자인");
    }

    @Test
    @DisplayName("Occupation 이름 수정 성공")
    void updateName_Success() {
        // when
        occupation.updateName("IT/소프트웨어");

        // then
        assertThat(occupation.getName()).isEqualTo("IT/소프트웨어");
    }

    @Test
    @DisplayName("Occupation 이름을 다른 이름으로 여러 번 수정 성공")
    void updateName_Multiple_Success() {
        // when
        occupation.updateName("첫번째 수정");
        occupation.updateName("두번째 수정");
        occupation.updateName("최종 수정");

        // then
        assertThat(occupation.getName()).isEqualTo("최종 수정");
    }
}