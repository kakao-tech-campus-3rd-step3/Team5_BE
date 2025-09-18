/* ----- 안전한 재생성을 위해 FK 역순으로 드롭 ----- */
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS user_flow_progress;
DROP TABLE IF EXISTS question_jobs;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS user_preferences;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS occupations;
DROP TABLE IF EXISTS users;

/* =========================
   USERS
   ========================= */
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100),
                       role ENUM('FREE','PAID','ADMIN') NOT NULL DEFAULT 'FREE',
                       streak INT NOT NULL DEFAULT 0,
                       solved_today TINYINT(1) NOT NULL DEFAULT 0,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   USER PREFERENCES
   - 사용자 대표 직군 1개 선택 (FK: jobs)
   ========================= */
CREATE TABLE user_preferences (
                              user_id BIGINT PRIMARY KEY,
                              daily_question_limit INT NOT NULL DEFAULT 1,
                              question_mode ENUM('TECH','FLOW') NOT NULL DEFAULT 'TECH',
                              user_response_type ENUM('VOICE','TEXT') NOT NULL DEFAULT 'TEXT',
                              time_limit_seconds INT DEFAULT 180,
                              notify_time TIME NULL,
                              allow_push TINYINT(1) NOT NULL DEFAULT 0,
                              user_job BIGINT NOT NULL,
                              CONSTRAINT fk_user_prefs_user
                                  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   OCCUPATIONS (상위 카테고리)
   ========================= */
CREATE TABLE occupations (
                             occupation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             occupation_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   JOBS (세부 직군)
   ========================= */
CREATE TABLE jobs (
                      job_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      job_name VARCHAR(100) NOT NULL UNIQUE,
                      occupation_id BIGINT NOT NULL,
                      CONSTRAINT fk_jobs_parent
                          FOREIGN KEY (occupation_id) REFERENCES occupations(occupation_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   QUESTIONS
   - 타입(TECH/FLOW) + 플로우 단계(해당 시)
   ========================= */
CREATE TABLE questions (
                           question_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           question_type ENUM('TECH','INTRO','MOTIVATION','PERSONALITY') NOT NULL,
                           question_text MEDIUMTEXT NOT NULL,
                           enabled TINYINT(1) NOT NULL DEFAULT 1,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           CONSTRAINT uq_questions_text UNIQUE (question_text(255)),
                           INDEX idx_questions_enabled_type (enabled, question_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   QUESTION-JOBS (다대다)
   ========================= */
CREATE TABLE question_jobs (
                               question_jobs_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                question_id BIGINT NOT NULL,
                               job_id BIGINT NOT NULL,
                               CONSTRAINT fk_qjobs_question
                                   FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
                               CONSTRAINT fk_qjobs_job
                                   FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   USER FLOW PROGRESS
   ========================= */
CREATE TABLE user_flow_progress (
                                    user_id BIGINT PRIMARY KEY,
                                    next_phase ENUM('INTRO','MOTIVATION','TECH1','TECH2','PERSONALITY') NOT NULL DEFAULT 'INTRO',
                                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_flow_progress_user
                                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   ANSWERS
   - 동일 유저/질문 중복 제출 방지
   ========================= */
CREATE TABLE answers (
                         answer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         question_id BIGINT NOT NULL,
                         answer_text MEDIUMTEXT NOT NULL, -- 오디오 변환 후 answer 생성
                         level TINYINT NULL,
                         starred TINYINT(1) NOT NULL DEFAULT 0, -- default 0
                         answered_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         memo MEDIUMTEXT NULL, -- 메모 필드 추가
                         CONSTRAINT ck_answers_level CHECK (level IS NULL OR (level BETWEEN 1 AND 5)),
    CONSTRAINT fk_answers_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question
        FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE RESTRICT,
    INDEX idx_answers_user_time (user_id, answered_time DESC),
    INDEX idx_answers_q_time (question_id, answered_time DESC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =========================
   ANSWER FEEDBACKS (LLM/STT 비동기)
   ========================= */
CREATE TABLE feedbacks (
                           feedback_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           answer_id BIGINT NOT NULL,
                           status ENUM('PENDING','DONE','FAILED') NOT NULL DEFAULT 'PENDING',
                           content MEDIUMTEXT NULL, -- entity 생성 후
                           latency_ms BIGINT NULL, -- entity 생성 후, 지연 시간 측정 필요
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           CONSTRAINT fk_feedback_answer
                               FOREIGN KEY (answer_id) REFERENCES answers(answer_id) ON DELETE CASCADE,
                           INDEX idx_feedback_answer_status (answer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
