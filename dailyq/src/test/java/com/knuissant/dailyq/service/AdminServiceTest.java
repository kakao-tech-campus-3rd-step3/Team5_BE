package com.knuissant.dailyq.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.jobs.Occupation;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserRole;
import com.knuissant.dailyq.dto.admin.JobManagementDto;
import com.knuissant.dailyq.dto.admin.OccupationManagementDto;
import com.knuissant.dailyq.dto.admin.QuestionManagementDto;
import com.knuissant.dailyq.dto.admin.UserManagementDto;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService 테스트")
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OccupationRepository occupationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private Occupation testOccupation;
    private Job testJob;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .role(UserRole.FREE)
                .streak(5)
                .solvedToday(false)
                .build();

        testOccupation = Occupation.builder()
                .id(1L)
                .name("IT/개발")
                .build();

        testJob = Job.builder()
                .id(1L)
                .name("백엔드 개발자")
                .occupation(testOccupation)
                .build();

        testQuestion = Question.builder()
                .id(1L)
                .questionText("테스트 질문")
                .questionType(QuestionType.TECH)
                .enabled(true)
                .jobs(new HashSet<>(Arrays.asList(testJob)))
                .build();
    }

    @Nested
    @DisplayName("사용자 관리 테스트")
    class UserManagementTests {

        @Test
        @DisplayName("모든 사용자 조회 성공")
        void getAllUsers_Success() {
            // given
            List<User> users = Arrays.asList(testUser);
            given(userRepository.findAll()).willReturn(users);

            // when
            List<UserManagementDto.UserResponse> result = adminService.getAllUsers();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).email()).isEqualTo("test@example.com");
            then(userRepository).should(times(1)).findAll();
        }

        @Test
        @DisplayName("특정 사용자 조회 성공")
        void getUserById_Success() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // when
            UserManagementDto.UserDetailResponse result = adminService.getUserById(1L);

            // then
            assertThat(result.userId()).isEqualTo(1L);
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.streak()).isEqualTo(5);
            then(userRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 예외 발생")
        void getUserById_NotFound_ThrowsException() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.getUserById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("사용자 정보 수정 성공")
        void updateUser_Success() {
            // given
            UserManagementDto.UserUpdateRequest request =
                    new UserManagementDto.UserUpdateRequest("Updated Name", UserRole.PREMIUM);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // when
            UserManagementDto.UserResponse result = adminService.updateUser(1L, request);

            // then
            assertThat(result.name()).isEqualTo("Updated Name");
            assertThat(result.role()).isEqualTo(UserRole.PREMIUM);
            then(userRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시 예외 발생")
        void updateUser_NotFound_ThrowsException() {
            // given
            UserManagementDto.UserUpdateRequest request =
                    new UserManagementDto.UserUpdateRequest("Updated Name", UserRole.PREMIUM);
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.updateUser(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("사용자 삭제 성공")
        void deleteUser_Success() {
            // given
            given(userRepository.existsById(1L)).willReturn(true);

            // when
            adminService.deleteUser(1L);

            // then
            then(userRepository).should(times(1)).existsById(1L);
            then(userRepository).should(times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 시 예외 발생")
        void deleteUser_NotFound_ThrowsException() {
            // given
            given(userRepository.existsById(999L)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminService.deleteUser(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

            then(userRepository).should(never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("직군 관리 테스트")
    class OccupationManagementTests {

        @Test
        @DisplayName("직군 생성 성공")
        void createOccupation_Success() {
            // given
            OccupationManagementDto.OccupationCreateRequest request =
                    new OccupationManagementDto.OccupationCreateRequest("IT/개발");
            given(occupationRepository.existsByName("IT/개발")).willReturn(false);
            given(occupationRepository.save(any(Occupation.class))).willReturn(testOccupation);

            // when
            OccupationManagementDto.OccupationResponse result = adminService.createOccupation(request);

            // then
            assertThat(result.occupationName()).isEqualTo("IT/개발");
            then(occupationRepository).should(times(1)).existsByName("IT/개발");
            then(occupationRepository).should(times(1)).save(any(Occupation.class));
        }

        @Test
        @DisplayName("중복된 직군명으로 생성 시 예외 발생")
        void createOccupation_AlreadyExists_ThrowsException() {
            // given
            OccupationManagementDto.OccupationCreateRequest request =
                    new OccupationManagementDto.OccupationCreateRequest("IT/개발");
            given(occupationRepository.existsByName("IT/개발")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.createOccupation(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OCCUPATION_ALREADY_EXISTS);

            then(occupationRepository).should(never()).save(any(Occupation.class));
        }

        @Test
        @DisplayName("직군 수정 성공")
        void updateOccupation_Success() {
            // given
            OccupationManagementDto.OccupationUpdateRequest request =
                    new OccupationManagementDto.OccupationUpdateRequest("IT/개발 (수정)");
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));
            given(occupationRepository.existsByName("IT/개발 (수정)")).willReturn(false);

            // when
            OccupationManagementDto.OccupationResponse result = adminService.updateOccupation(1L, request);

            // then
            assertThat(result.occupationName()).isEqualTo("IT/개발 (수정)");
            then(occupationRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 직군 수정 시 예외 발생")
        void updateOccupation_NotFound_ThrowsException() {
            // given
            OccupationManagementDto.OccupationUpdateRequest request =
                    new OccupationManagementDto.OccupationUpdateRequest("IT/개발");
            given(occupationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.updateOccupation(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OCCUPATION_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 직군이 이미 사용 중인 이름으로 수정 시 예외 발생")
        void updateOccupation_NameAlreadyExists_ThrowsException() {
            // given
            OccupationManagementDto.OccupationUpdateRequest request =
                    new OccupationManagementDto.OccupationUpdateRequest("다른 직군");
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));
            given(occupationRepository.existsByName("다른 직군")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.updateOccupation(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OCCUPATION_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("직군 삭제 성공")
        void deleteOccupation_Success() {
            // given
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));
            given(jobRepository.existsByOccupation(testOccupation)).willReturn(false);

            // when
            adminService.deleteOccupation(1L);

            // then
            then(occupationRepository).should(times(1)).findById(1L);
            then(jobRepository).should(times(1)).existsByOccupation(testOccupation);
            then(occupationRepository).should(times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 직군 삭제 시 예외 발생")
        void deleteOccupation_NotFound_ThrowsException() {
            // given
            given(occupationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.deleteOccupation(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OCCUPATION_NOT_FOUND);

            then(occupationRepository).should(never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("하위 직업이 있는 직군 삭제 시 예외 발생")
        void deleteOccupation_HasJobs_ThrowsException() {
            // given
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));
            given(jobRepository.existsByOccupation(testOccupation)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.deleteOccupation(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_OCCUPATION_WITH_JOBS);

            then(occupationRepository).should(never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("직업 관리 테스트")
    class JobManagementTests {

        @Test
        @DisplayName("직업 생성 성공")
        void createJob_Success() {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("프론트엔드 개발자", 1L);
            given(jobRepository.existsByName("프론트엔드 개발자")).willReturn(false);
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));
            given(jobRepository.save(any(Job.class))).willReturn(testJob);

            // when
            JobManagementDto.JobResponse result = adminService.createJob(request);

            // then
            assertThat(result.jobName()).isEqualTo("백엔드 개발자");
            then(jobRepository).should(times(1)).existsByName("프론트엔드 개발자");
            then(occupationRepository).should(times(1)).findById(1L);
            then(jobRepository).should(times(1)).save(any(Job.class));
        }

        @Test
        @DisplayName("중복된 직업명으로 생성 시 예외 발생")
        void createJob_AlreadyExists_ThrowsException() {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("백엔드 개발자", 1L);
            given(jobRepository.existsByName("백엔드 개발자")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.createJob(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.JOB_ALREADY_EXISTS);

            then(jobRepository).should(never()).save(any(Job.class));
        }

        @Test
        @DisplayName("존재하지 않는 직군으로 직업 생성 시 예외 발생")
        void createJob_OccupationNotFound_ThrowsException() {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("백엔드 개발자", 999L);
            given(jobRepository.existsByName("백엔드 개발자")).willReturn(false);
            given(occupationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.createJob(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OCCUPATION_NOT_FOUND);

            then(jobRepository).should(never()).save(any(Job.class));
        }

        @Test
        @DisplayName("직업 수정 성공")
        void updateJob_Success() {
            // given
            JobManagementDto.JobUpdateRequest request =
                    new JobManagementDto.JobUpdateRequest("풀스택 개발자", 1L);
            given(jobRepository.findById(1L)).willReturn(Optional.of(testJob));
            given(jobRepository.existsByName("풀스택 개발자")).willReturn(false);
            given(occupationRepository.findById(1L)).willReturn(Optional.of(testOccupation));

            // when
            JobManagementDto.JobResponse result = adminService.updateJob(1L, request);

            // then
            assertThat(result.jobName()).isEqualTo("풀스택 개발자");
            then(jobRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 직업 수정 시 예외 발생")
        void updateJob_NotFound_ThrowsException() {
            // given
            JobManagementDto.JobUpdateRequest request =
                    new JobManagementDto.JobUpdateRequest("백엔드 개발자", 1L);
            given(jobRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.updateJob(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.JOB_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 직업이 이미 사용 중인 이름으로 수정 시 예외 발생")
        void updateJob_NameAlreadyExists_ThrowsException() {
            // given
            JobManagementDto.JobUpdateRequest request =
                    new JobManagementDto.JobUpdateRequest("다른 직업", 1L);
            given(jobRepository.findById(1L)).willReturn(Optional.of(testJob));
            given(jobRepository.existsByName("다른 직업")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.updateJob(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.JOB_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("직업 삭제 성공")
        void deleteJob_Success() {
            // given
            given(jobRepository.findById(1L)).willReturn(Optional.of(testJob));
            given(userPreferencesRepository.existsByUserJob(testJob)).willReturn(false);

            // when
            adminService.deleteJob(1L);

            // then
            then(jobRepository).should(times(1)).findById(1L);
            then(userPreferencesRepository).should(times(1)).existsByUserJob(testJob);
            then(jobRepository).should(times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 직업 삭제 시 예외 발생")
        void deleteJob_NotFound_ThrowsException() {
            // given
            given(jobRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.deleteJob(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.JOB_NOT_FOUND);

            then(jobRepository).should(never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("사용 중인 직업 삭제 시 예외 발생")
        void deleteJob_InUse_ThrowsException() {
            // given
            given(jobRepository.findById(1L)).willReturn(Optional.of(testJob));
            given(userPreferencesRepository.existsByUserJob(testJob)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.deleteJob(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_JOB_IN_USE);

            then(jobRepository).should(never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("질문 관리 테스트")
    class QuestionManagementTests {

        @Test
        @DisplayName("모든 질문 조회 성공")
        void getAllQuestions_Success() {
            // given
            List<Question> questions = Arrays.asList(testQuestion);
            given(questionRepository.findAll()).willReturn(questions);

            // when
            List<QuestionManagementDto.QuestionResponse> result = adminService.getAllQuestions();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).questionText()).isEqualTo("테스트 질문");
            then(questionRepository).should(times(1)).findAll();
        }

        @Test
        @DisplayName("질문 생성 성공")
        void createQuestion_Success() {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "새로운 질문", QuestionType.TECH, Arrays.asList(1L, 2L)
                    );
            Job job2 = Job.builder().id(2L).name("프론트엔드 개발자").occupation(testOccupation).build();
            List<Job> jobs = Arrays.asList(testJob, job2);

            given(questionRepository.existsByQuestionText("새로운 질문")).willReturn(false);
            given(jobRepository.findAllById(Arrays.asList(1L, 2L))).willReturn(jobs);
            given(questionRepository.save(any(Question.class))).willReturn(testQuestion);

            // when
            QuestionManagementDto.QuestionDetailResponse result = adminService.createQuestion(request);

            // then
            assertThat(result.questionText()).isEqualTo("테스트 질문");
            then(questionRepository).should(times(1)).existsByQuestionText("새로운 질문");
            then(jobRepository).should(times(1)).findAllById(Arrays.asList(1L, 2L));
            then(questionRepository).should(times(1)).save(any(Question.class));
        }

        @Test
        @DisplayName("중복된 질문 내용으로 생성 시 예외 발생")
        void createQuestion_AlreadyExists_ThrowsException() {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "테스트 질문", QuestionType.TECH, Arrays.asList(1L)
                    );
            given(questionRepository.existsByQuestionText("테스트 질문")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.createQuestion(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.QUESTION_ALREADY_EXISTS);

            then(questionRepository).should(never()).save(any(Question.class));
        }

        @Test
        @DisplayName("존재하지 않는 직업 ID로 질문 생성 시 예외 발생")
        void createQuestion_JobNotFound_ThrowsException() {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "새로운 질문", QuestionType.TECH, Arrays.asList(1L, 999L)
                    );
            List<Job> jobs = Arrays.asList(testJob);  // Only one job found

            given(questionRepository.existsByQuestionText("새로운 질문")).willReturn(false);
            given(jobRepository.findAllById(Arrays.asList(1L, 999L))).willReturn(jobs);

            // when & then
            assertThatThrownBy(() -> adminService.createQuestion(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.JOB_NOT_FOUND);

            then(questionRepository).should(never()).save(any(Question.class));
        }

        @Test
        @DisplayName("질문 수정 성공")
        void updateQuestion_Success() {
            // given
            QuestionManagementDto.QuestionUpdateRequest request =
                    new QuestionManagementDto.QuestionUpdateRequest(
                            "수정된 질문", QuestionType.PERSONALITY, false, Arrays.asList(1L)
                    );
            given(questionRepository.findById(1L)).willReturn(Optional.of(testQuestion));
            given(questionRepository.existsByQuestionText("수정된 질문")).willReturn(false);
            given(jobRepository.findAllById(Arrays.asList(1L))).willReturn(Arrays.asList(testJob));

            // when
            QuestionManagementDto.QuestionDetailResponse result = adminService.updateQuestion(1L, request);

            // then
            assertThat(result.questionText()).isEqualTo("수정된 질문");
            assertThat(result.questionType()).isEqualTo(QuestionType.PERSONALITY);
            assertThat(result.enabled()).isEqualTo(false);
            then(questionRepository).should(times(1)).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 질문 수정 시 예외 발생")
        void updateQuestion_NotFound_ThrowsException() {
            // given
            QuestionManagementDto.QuestionUpdateRequest request =
                    new QuestionManagementDto.QuestionUpdateRequest(
                            "수정된 질문", QuestionType.TECH, true, Arrays.asList(1L)
                    );
            given(questionRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.updateQuestion(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.QUESTION_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 질문이 이미 사용 중인 내용으로 수정 시 예외 발생")
        void updateQuestion_TextAlreadyExists_ThrowsException() {
            // given
            QuestionManagementDto.QuestionUpdateRequest request =
                    new QuestionManagementDto.QuestionUpdateRequest(
                            "다른 질문", QuestionType.TECH, true, Arrays.asList(1L)
                    );
            given(questionRepository.findById(1L)).willReturn(Optional.of(testQuestion));
            given(questionRepository.existsByQuestionText("다른 질문")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.updateQuestion(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.QUESTION_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("질문 삭제 성공")
        void deleteQuestion_Success() {
            // given
            given(questionRepository.findById(1L)).willReturn(Optional.of(testQuestion));
            given(answerRepository.existsByQuestion(testQuestion)).willReturn(false);

            // when
            adminService.deleteQuestion(1L);

            // then
            then(questionRepository).should(times(1)).findById(1L);
            then(answerRepository).should(times(1)).existsByQuestion(testQuestion);
            then(questionRepository).should(times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 질문 삭제 시 예외 발생")
        void deleteQuestion_NotFound_ThrowsException() {
            // given
            given(questionRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminService.deleteQuestion(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.QUESTION_NOT_FOUND);

            then(questionRepository).should(never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("답변이 있는 질문 삭제 시 예외 발생")
        void deleteQuestion_HasAnswers_ThrowsException() {
            // given
            given(questionRepository.findById(1L)).willReturn(Optional.of(testQuestion));
            given(answerRepository.existsByQuestion(testQuestion)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> adminService.deleteQuestion(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_QUESTION_WITH_ANSWERS);

            then(questionRepository).should(never()).deleteById(anyLong());
        }
    }
}