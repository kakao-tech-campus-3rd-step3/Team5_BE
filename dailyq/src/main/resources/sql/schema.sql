-- Schema DDL (manual run). Spring Boot won't auto-run files in resources/sql.

# SET GLOBAL time_zone = 'Asia/Seoul';

-- =====================================================================
-- Auth Service: 사용자 계정/소셜 계정/리프레시 토큰 관리
-- =====================================================================

-- users: 기본 사용자 계정(이메일 기반)
CREATE TABLE IF NOT EXISTS users (
                                     u_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE,
                                     password_hash VARCHAR(255) NULL,
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- oauth_accounts: 소셜 로그인 계정 매핑
CREATE TABLE IF NOT EXISTS oauth_accounts (
                                              oa_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              u_id BIGINT NOT NULL,
                                              provider ENUM('GOOGLE','KAKAO') NOT NULL,
                                              provider_user_id VARCHAR(191) NOT NULL,
                                              UNIQUE KEY uq_oauth_provider_user (provider, provider_user_id),
                                              KEY idx_oauth_user (u_id)
);

-- refresh_tokens: 리프레시 토큰 저장/만료/폐기 상태
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              rt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              u_id BIGINT NOT NULL,
                                              token VARCHAR(255) NOT NULL,
                                              expires_at DATETIME NOT NULL,
                                              revoked TINYINT(1) NOT NULL DEFAULT 0,
                                              KEY idx_rt_user (u_id),
                                              KEY idx_rt_expires (expires_at)
);

-- =====================================================================
-- Question Service: 직군/질문 카탈로그
-- =====================================================================

-- jobs: 직군 마스터(FRONTEND/BACKEND/IOS/ANDROID/DATA)
CREATE TABLE IF NOT EXISTS jobs (
                                    j_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(30) NOT NULL UNIQUE,
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- questions: 질문 카탈로그(직군/유형/플로우 단계/주제)
CREATE TABLE IF NOT EXISTS questions (
                                         q_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         j_id BIGINT NOT NULL,
                                         content TEXT NOT NULL UNIQUE ,
                                         type ENUM('TECH','NON_TECH') NOT NULL,
                                         flow_phase ENUM('INTRO','MOTIVATION','TECH1','TECH2','PERSONALITY') NULL,
                                         topic VARCHAR(50) NULL,
                                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         CONSTRAINT fk_questions_job FOREIGN KEY (j_id) REFERENCES jobs(j_id)
);

-- 질문 조회 최적화를 위한 인덱스(모드별/플로우 단계별)
CREATE INDEX idx_questions_job_type  ON questions (j_id, type);
CREATE INDEX idx_questions_job_phase ON questions (j_id, flow_phase);

-- =====================================================================
-- Profile/Progress Service: 사용자 프로필/설정/직군선호/라이벌/스트릭 상태 저장
-- =====================================================================

-- user_profiles: 기본 프로필(닉네임/아바타)
CREATE TABLE IF NOT EXISTS user_profiles (
                                             u_id BIGINT PRIMARY KEY,
                                             nickname VARCHAR(50) NULL,
                                             avatar_url VARCHAR(255) NULL,
                                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- user_settings: 일일 질문 모드/답변 방식/시간 제한/알림 설정
CREATE TABLE IF NOT EXISTS user_settings (
                                             u_id BIGINT PRIMARY KEY,
                                             default_mode ENUM('TECH','FLOW') NOT NULL DEFAULT 'TECH',
                                             answer_method ENUM('TYPING','VOICE') NOT NULL DEFAULT 'TYPING',
                                             time_limit_sec INT NOT NULL DEFAULT 180,
                                             notify_daily_time TIME NULL,
                                             allow_push TINYINT(1) NOT NULL DEFAULT 1,
                                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- user_job_preferences: 사용자별 직군 선호(다중 선택)
CREATE TABLE IF NOT EXISTS user_job_preferences (
                                                    u_id BIGINT NOT NULL,
                                                    job_role ENUM('FRONTEND','BACKEND','IOS','ANDROID','DATA') NOT NULL,
                                                    PRIMARY KEY (u_id, job_role)
);

-- user_rivals: 단방향 라이벌 지정
CREATE TABLE IF NOT EXISTS user_rivals (
                                           u_id BIGINT NOT NULL,
                                           rival_u_id BIGINT NOT NULL,
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (u_id, rival_u_id)
);

-- user_streaks: 스트릭(현재/최대/마지막 답변일)
CREATE TABLE IF NOT EXISTS user_streaks (
                                            u_id BIGINT PRIMARY KEY,
                                            current_streak INT NOT NULL DEFAULT 0,
                                            best_streak INT NOT NULL DEFAULT 0,
                                            last_answer_date DATE NULL,
                                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================================
-- Answer Service: 사용자가 제출한 답변/오디오/난이도/즐겨찾기
-- =====================================================================

-- answers: 사용자의 질문별 답변 기록(텍스트/오디오/STT)
CREATE TABLE IF NOT EXISTS answers (
                                       a_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       u_id BIGINT NOT NULL,
                                       q_id BIGINT NOT NULL,
                                       answer_text MEDIUMTEXT NULL,
                                       audio_url VARCHAR(255) NULL,
                                       stt_text MEDIUMTEXT NULL,
                                       duration_sec INT NULL,
                                       starred TINYINT(1) NOT NULL DEFAULT 0,
                                       diff TINYINT NULL,
                                       solved_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       KEY idx_answers_user_question (u_id, q_id),
                                       KEY idx_answers_time (solved_time),
                                       KEY idx_answers_starred (u_id, starred)
);

-- =====================================================================
-- Feedback Service: LLM 피드백 및 꼬리질문 저장
-- =====================================================================

-- feedbacks: LLM 피드백(강점/개선/키워드/지연)
CREATE TABLE IF NOT EXISTS feedbacks (
                                         f_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         a_id BIGINT NOT NULL,
                                         model VARCHAR(50) NOT NULL,
                                         latency_ms INT NULL,
                                         score TINYINT NULL,
                                         strengths JSON NULL,
                                         improvements JSON NULL,
                                         keywords JSON NULL,
                                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         UNIQUE KEY uq_feedback_answer (a_id)
);

-- followup_questions: 꼬리질문(랭크 순)
CREATE TABLE IF NOT EXISTS followup_questions (
                                                  fq_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  a_id BIGINT NOT NULL,
                                                  question TEXT NOT NULL,
                                                  fq_rank TINYINT NOT NULL,
                                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                  KEY idx_fq_answer (a_id)
);

-- =====================================================================
-- Mentor Service: 멘토 정보/의뢰/리뷰
-- =====================================================================

-- mentors: 멘토 사용자 정보(요약/평점)
CREATE TABLE IF NOT EXISTS mentors (
                                       m_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       u_id BIGINT NOT NULL,
                                       bio TEXT NULL,
                                       rating_avg DECIMAL(3,2) NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- mentor_orders: 첨삭 의뢰(상태/배정/가격)
CREATE TABLE IF NOT EXISTS mentor_orders (
                                             mo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             requester_u_id BIGINT NOT NULL,
                                             a_id BIGINT NOT NULL,
                                             price_cents INT NOT NULL,
                                             currency CHAR(3) NOT NULL DEFAULT 'KRW',
                                             status ENUM('REQUESTED','ASSIGNED','DONE','CANCELLED','REFUNDED') NOT NULL DEFAULT 'REQUESTED',
                                             assigned_m_id BIGINT NULL,
                                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             KEY idx_mo_user_status (requester_u_id, status)
);

-- mentor_reviews: 멘토 리뷰(주문당 1회)
CREATE TABLE IF NOT EXISTS mentor_reviews (
                                              mr_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              mo_id BIGINT NOT NULL,
                                              rating TINYINT NOT NULL,
                                              comment TEXT NULL,
                                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              UNIQUE KEY uq_review_order (mo_id)
);

-- =====================================================================
-- Payment Service: 상품/구독/결제
-- =====================================================================

-- products: 판매 상품 정의(요금제/건당 등)
CREATE TABLE IF NOT EXISTS products (
                                        p_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        code VARCHAR(50) UNIQUE NOT NULL,
                                        name VARCHAR(100) NOT NULL,
                                        price_cents INT NOT NULL,
                                        currency CHAR(3) NOT NULL DEFAULT 'KRW',
                                        p_interval ENUM('ONE_TIME','MONTH','YEAR') NOT NULL DEFAULT 'MONTH',
                                        active TINYINT(1) NOT NULL DEFAULT 1,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- subscriptions: 구독 상태(유효기간/다음 결제일)
CREATE TABLE IF NOT EXISTS subscriptions (
                                             s_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             u_id BIGINT NOT NULL,
                                             p_id BIGINT NOT NULL,
                                             status ENUM('ACTIVE','PAUSED','CANCELLED','EXPIRED') NOT NULL,
                                             start_at DATETIME NOT NULL,
                                             end_at DATETIME NULL,
                                             next_billing_at DATETIME NULL,
                                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             KEY idx_sub_user_status (u_id, status)
);

-- payments: 결제 트랜잭션(단건/구독 결제 영수증)
CREATE TABLE IF NOT EXISTS payments (
                                        pay_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        u_id BIGINT NOT NULL,
                                        p_id BIGINT NULL,
                                        amount_cents INT NOT NULL,
                                        currency CHAR(3) NOT NULL DEFAULT 'KRW',
                                        provider VARCHAR(30) NOT NULL,
                                        provider_txn_id VARCHAR(100) NULL,
                                        status ENUM('PENDING','PAID','FAILED','REFUNDED') NOT NULL,
                                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        KEY idx_pay_user_status (u_id, status)
);

-- =====================================================================
-- Notification Service: 푸시 토큰/발송/로그
-- =====================================================================

-- push_tokens: 사용자별 푸시 토큰
CREATE TABLE IF NOT EXISTS push_tokens (
                                           pt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           u_id BIGINT NOT NULL,
                                           device VARCHAR(30) NULL,
                                           token VARCHAR(255) NOT NULL,
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           UNIQUE KEY uq_push_token (u_id, token)
);

-- notifications: 알림 엔티티(예약/상태)
CREATE TABLE IF NOT EXISTS notifications (
                                             n_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             u_id BIGINT NOT NULL,
                                             title VARCHAR(120) NOT NULL,
                                             body VARCHAR(500) NOT NULL,
                                             channel ENUM('PUSH','EMAIL','SMS') NOT NULL DEFAULT 'PUSH',
                                             scheduled_at DATETIME NULL,
                                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             status ENUM('SCHEDULED','SENT','FAILED') NOT NULL DEFAULT 'SCHEDULED',
                                             KEY idx_notif_user_status (u_id, status),
                                             KEY idx_notif_scheduled (scheduled_at)
);

-- notification_logs: 발송 결과 로그
CREATE TABLE IF NOT EXISTS notification_logs (
                                                 nl_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 n_id BIGINT NOT NULL,
                                                 pt_id BIGINT NULL,
                                                 status ENUM('SENT','FAILED') NOT NULL,
                                                 error_msg VARCHAR(255) NULL,
                                                 sent_at DATETIME NULL,
                                                 KEY idx_nlog_notif (n_id)
);

-- =====================================================================
-- Batch/Aggregation: 질문 난이도 집계(avg_level)
-- =====================================================================

-- question_level_agg: 사용자 난이도 평가 집계 테이블(배치 산출)
CREATE TABLE IF NOT EXISTS question_level_agg (
                                                  q_id BIGINT PRIMARY KEY,
                                                  avg_level DECIMAL(3,2) NOT NULL,
                                                  votes INT NOT NULL,
                                                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

