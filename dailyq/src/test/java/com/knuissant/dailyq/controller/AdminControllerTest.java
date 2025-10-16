package com.knuissant.dailyq.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuissant.dailyq.dto.admin.JobManagementDto;
import com.knuissant.dailyq.dto.admin.OccupationManagementDto;
import com.knuissant.dailyq.dto.admin.QuestionManagementDto;
import com.knuissant.dailyq.dto.admin.UserManagementDto;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.UserRole;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.service.AdminService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@DisplayName("AdminController 테스트")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Nested
    @DisplayName("사용자 관리 API 테스트")
    class UserManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("모든 사용자 조회 성공")
        void getAllUsers_Success() throws Exception {
            // given
            List<UserManagementDto.UserResponse> users = Arrays.asList(
                    new UserManagementDto.UserResponse(1L, "user1@test.com", "User 1", UserRole.FREE),
                    new UserManagementDto.UserResponse(2L, "user2@test.com", "User 2", UserRole.PREMIUM)
            );
            given(adminService.getAllUsers()).willReturn(users);

            // when & then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].userId").value(1))
                    .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                    .andExpect(jsonPath("$[1].userId").value(2));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("특정 사용자 조회 성공")
        void getUserById_Success() throws Exception {
            // given
            Long userId = 1L;
            UserManagementDto.UserDetailResponse user = new UserManagementDto.UserDetailResponse(
                    userId, "user@test.com", "Test User", UserRole.FREE,
                    5, false, LocalDateTime.now(), LocalDateTime.now()
            );
            given(adminService.getUserById(userId)).willReturn(user);

            // when & then
            mockMvc.perform(get("/api/admin/users/{userId}", userId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.email").value("user@test.com"))
                    .andExpect(jsonPath("$.streak").value(5));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("존재하지 않는 사용자 조회 시 404 에러")
        void getUserById_NotFound() throws Exception {
            // given
            Long userId = 999L;
            given(adminService.getUserById(userId))
                    .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/admin/users/{userId}", userId))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("사용자 정보 수정 성공")
        void updateUser_Success() throws Exception {
            // given
            Long userId = 1L;
            UserManagementDto.UserUpdateRequest request = new UserManagementDto.UserUpdateRequest(
                    "Updated Name", UserRole.PREMIUM
            );
            UserManagementDto.UserResponse response = new UserManagementDto.UserResponse(
                    userId, "user@test.com", "Updated Name", UserRole.PREMIUM
            );
            given(adminService.updateUser(eq(userId), any(UserManagementDto.UserUpdateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(put("/api/admin/users/{userId}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.role").value("PREMIUM"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("사용자 정보 수정 시 유효성 검증 실패")
        void updateUser_ValidationFailed() throws Exception {
            // given
            Long userId = 1L;
            UserManagementDto.UserUpdateRequest request = new UserManagementDto.UserUpdateRequest(
                    "", UserRole.PREMIUM  // 빈 이름
            );

            // when & then
            mockMvc.perform(put("/api/admin/users/{userId}", userId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("사용자 삭제 성공")
        void deleteUser_Success() throws Exception {
            // given
            Long userId = 1L;
            willDoNothing().given(adminService).deleteUser(userId);

            // when & then
            mockMvc.perform(delete("/api/admin/users/{userId}", userId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("존재하지 않는 사용자 삭제 시 404 에러")
        void deleteUser_NotFound() throws Exception {
            // given
            Long userId = 999L;
            willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                    .given(adminService).deleteUser(userId);

            // when & then
            mockMvc.perform(delete("/api/admin/users/{userId}", userId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("직군 관리 API 테스트")
    class OccupationManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직군 생성 성공")
        void createOccupation_Success() throws Exception {
            // given
            OccupationManagementDto.OccupationCreateRequest request =
                    new OccupationManagementDto.OccupationCreateRequest("IT/개발");
            OccupationManagementDto.OccupationResponse response =
                    new OccupationManagementDto.OccupationResponse(1L, "IT/개발");
            given(adminService.createOccupation(any(OccupationManagementDto.OccupationCreateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/admin/occupations")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.occupationId").value(1))
                    .andExpect(jsonPath("$.occupationName").value("IT/개발"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("중복된 직군명으로 생성 시 409 에러")
        void createOccupation_AlreadyExists() throws Exception {
            // given
            OccupationManagementDto.OccupationCreateRequest request =
                    new OccupationManagementDto.OccupationCreateRequest("IT/개발");
            given(adminService.createOccupation(any(OccupationManagementDto.OccupationCreateRequest.class)))
                    .willThrow(new BusinessException(ErrorCode.OCCUPATION_ALREADY_EXISTS));

            // when & then
            mockMvc.perform(post("/api/admin/occupations")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직군 수정 성공")
        void updateOccupation_Success() throws Exception {
            // given
            Long occupationId = 1L;
            OccupationManagementDto.OccupationUpdateRequest request =
                    new OccupationManagementDto.OccupationUpdateRequest("IT/개발 (수정)");
            OccupationManagementDto.OccupationResponse response =
                    new OccupationManagementDto.OccupationResponse(occupationId, "IT/개발 (수정)");
            given(adminService.updateOccupation(eq(occupationId), any(OccupationManagementDto.OccupationUpdateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(put("/api/admin/occupations/{occupationId}", occupationId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.occupationName").value("IT/개발 (수정)"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직군 삭제 성공")
        void deleteOccupation_Success() throws Exception {
            // given
            Long occupationId = 1L;
            willDoNothing().given(adminService).deleteOccupation(occupationId);

            // when & then
            mockMvc.perform(delete("/api/admin/occupations/{occupationId}", occupationId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("하위 직업이 있는 직군 삭제 시 400 에러")
        void deleteOccupation_HasJobs() throws Exception {
            // given
            Long occupationId = 1L;
            willThrow(new BusinessException(ErrorCode.CANNOT_DELETE_OCCUPATION_WITH_JOBS))
                    .given(adminService).deleteOccupation(occupationId);

            // when & then
            mockMvc.perform(delete("/api/admin/occupations/{occupationId}", occupationId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("직업 관리 API 테스트")
    class JobManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직업 생성 성공")
        void createJob_Success() throws Exception {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("백엔드 개발자", 1L);
            JobManagementDto.JobResponse response =
                    new JobManagementDto.JobResponse(1L, "백엔드 개발자", 1L, "IT/개발");
            given(adminService.createJob(any(JobManagementDto.JobCreateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/admin/jobs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.jobId").value(1))
                    .andExpect(jsonPath("$.jobName").value("백엔드 개발자"))
                    .andExpect(jsonPath("$.occupationId").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("중복된 직업명으로 생성 시 409 에러")
        void createJob_AlreadyExists() throws Exception {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("백엔드 개발자", 1L);
            given(adminService.createJob(any(JobManagementDto.JobCreateRequest.class)))
                    .willThrow(new BusinessException(ErrorCode.JOB_ALREADY_EXISTS));

            // when & then
            mockMvc.perform(post("/api/admin/jobs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("존재하지 않는 직군으로 직업 생성 시 404 에러")
        void createJob_OccupationNotFound() throws Exception {
            // given
            JobManagementDto.JobCreateRequest request =
                    new JobManagementDto.JobCreateRequest("백엔드 개발자", 999L);
            given(adminService.createJob(any(JobManagementDto.JobCreateRequest.class)))
                    .willThrow(new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

            // when & then
            mockMvc.perform(post("/api/admin/jobs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직업 수정 성공")
        void updateJob_Success() throws Exception {
            // given
            Long jobId = 1L;
            JobManagementDto.JobUpdateRequest request =
                    new JobManagementDto.JobUpdateRequest("풀스택 개발자", 1L);
            JobManagementDto.JobResponse response =
                    new JobManagementDto.JobResponse(jobId, "풀스택 개발자", 1L, "IT/개발");
            given(adminService.updateJob(eq(jobId), any(JobManagementDto.JobUpdateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(put("/api/admin/jobs/{jobId}", jobId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.jobName").value("풀스택 개발자"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("직업 삭제 성공")
        void deleteJob_Success() throws Exception {
            // given
            Long jobId = 1L;
            willDoNothing().given(adminService).deleteJob(jobId);

            // when & then
            mockMvc.perform(delete("/api/admin/jobs/{jobId}", jobId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("사용 중인 직업 삭제 시 400 에러")
        void deleteJob_InUse() throws Exception {
            // given
            Long jobId = 1L;
            willThrow(new BusinessException(ErrorCode.CANNOT_DELETE_JOB_IN_USE))
                    .given(adminService).deleteJob(jobId);

            // when & then
            mockMvc.perform(delete("/api/admin/jobs/{jobId}", jobId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("질문 관리 API 테스트")
    class QuestionManagementTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("모든 질문 조회 성공")
        void getAllQuestions_Success() throws Exception {
            // given
            List<QuestionManagementDto.QuestionResponse> questions = Arrays.asList(
                    new QuestionManagementDto.QuestionResponse(1L, "질문 1", QuestionType.TECH, true),
                    new QuestionManagementDto.QuestionResponse(2L, "질문 2", QuestionType.PERSONALITY, true)
            );
            given(adminService.getAllQuestions()).willReturn(questions);

            // when & then
            mockMvc.perform(get("/api/admin/questions"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("질문 생성 성공")
        void createQuestion_Success() throws Exception {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "새로운 기술 질문", QuestionType.TECH, Arrays.asList(1L, 2L)
                    );
            Set<Long> jobIds = new HashSet<>(Arrays.asList(1L, 2L));
            QuestionManagementDto.QuestionDetailResponse response =
                    new QuestionManagementDto.QuestionDetailResponse(
                            1L, "새로운 기술 질문", QuestionType.TECH, true, jobIds
                    );
            given(adminService.createQuestion(any(QuestionManagementDto.QuestionCreateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/admin/questions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.questionId").value(1))
                    .andExpect(jsonPath("$.questionText").value("새로운 기술 질문"))
                    .andExpect(jsonPath("$.questionType").value("TECH"))
                    .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("중복된 질문 내용으로 생성 시 409 에러")
        void createQuestion_AlreadyExists() throws Exception {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "중복 질문", QuestionType.TECH, Arrays.asList(1L)
                    );
            given(adminService.createQuestion(any(QuestionManagementDto.QuestionCreateRequest.class)))
                    .willThrow(new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS));

            // when & then
            mockMvc.perform(post("/api/admin/questions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("질문 생성 시 직업 ID 목록이 비어있으면 400 에러")
        void createQuestion_EmptyJobIds() throws Exception {
            // given
            QuestionManagementDto.QuestionCreateRequest request =
                    new QuestionManagementDto.QuestionCreateRequest(
                            "질문", QuestionType.TECH, Arrays.asList()
                    );

            // when & then
            mockMvc.perform(post("/api/admin/questions")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("질문 수정 성공")
        void updateQuestion_Success() throws Exception {
            // given
            Long questionId = 1L;
            QuestionManagementDto.QuestionUpdateRequest request =
                    new QuestionManagementDto.QuestionUpdateRequest(
                            "수정된 질문", QuestionType.PERSONALITY, false, Arrays.asList(1L)
                    );
            Set<Long> jobIds = new HashSet<>(Arrays.asList(1L));
            QuestionManagementDto.QuestionDetailResponse response =
                    new QuestionManagementDto.QuestionDetailResponse(
                            questionId, "수정된 질문", QuestionType.PERSONALITY, false, jobIds
                    );
            given(adminService.updateQuestion(eq(questionId), any(QuestionManagementDto.QuestionUpdateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(put("/api/admin/questions/{questionId}", questionId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.questionText").value("수정된 질문"))
                    .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("질문 삭제 성공")
        void deleteQuestion_Success() throws Exception {
            // given
            Long questionId = 1L;
            willDoNothing().given(adminService).deleteQuestion(questionId);

            // when & then
            mockMvc.perform(delete("/api/admin/questions/{questionId}", questionId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("답변이 있는 질문 삭제 시 400 에러")
        void deleteQuestion_HasAnswers() throws Exception {
            // given
            Long questionId = 1L;
            willThrow(new BusinessException(ErrorCode.CANNOT_DELETE_QUESTION_WITH_ANSWERS))
                    .given(adminService).deleteQuestion(questionId);

            // when & then
            mockMvc.perform(delete("/api/admin/questions/{questionId}", questionId)
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("보안 테스트")
    class SecurityTests {

        @Test
        @DisplayName("인증되지 않은 사용자의 접근 차단")
        void accessWithoutAuthentication_Denied() throws Exception {
            // when & then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("ADMIN 권한이 없는 사용자의 접근 차단")
        void accessWithoutAdminRole_Denied() throws Exception {
            // when & then
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}