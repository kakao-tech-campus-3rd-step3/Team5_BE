/* -------------------------------------
   목업 데이터 (각 테이블 5개씩)
   ------------------------------------- */

/* USERS (5) */
INSERT INTO users (user_id, email, name, role, streak, solved_today)
VALUES (1, 'alice@example.com', 'Alice', 'FREE', 3, 0),
       (2, 'bob@example.com', 'Bob', 'PAID', 10, 1),
       (3, 'carol@example.com', 'Carol', 'FREE', 1, 0),
       (4, 'dave@example.com', 'Dave', 'PAID', 7, 1),
       (5, 'erin@example.com', 'Erin', 'ADMIN', 0, 0);

/* OCCUPATIONS 상위 카테고리 (5) */
INSERT INTO occupations (occupation_id, occupation_name)
VALUES (1, '개발'),
       (2, '데이터'),
       (3, '디자인'),
       (4, '마케팅'),
       (5, '운영');

/* JOBS 세부 직군 (5) */
INSERT INTO jobs (job_id, job_name, occupation_id)
VALUES (1, '백엔드 개발자', 1),
       (2, '프론트엔드 개발자', 1),
       (3, '데이터 사이언티스트', 2),
       (4, '프로덕트 디자이너', 3),
       (5, '그로스 마케터', 4);

/* USER PREFERENCES (5) — 대표 직군 매칭 */
INSERT INTO user_preferences (user_id, daily_question_limit, question_mode, user_response_type,
                              time_limit_seconds, notify_time, allow_push, user_job)
VALUES (1, 1, 'TECH', 'TEXT', 180, '09:00:00', 1, 1),
       (2, 10, 'FLOW', 'VOICE', 120, '08:30:00', 1, 2),
       (3, 1, 'TECH', 'TEXT', 180, 0, 1, 3),
       (4, 1, 'FLOW', 'VOICE', 90, '20:00:00', 0, 4),
       (5, 10, 'TECH', 'TEXT', 180, '07:30:00', 0, 5);

/* QUESTIONS (5) */
INSERT INTO questions (question_id, question_type, question_text, enabled)
VALUES (1, 'TECH', 'HTTP와 HTTPS의 차이와 TLS 핸드셰이크 과정을 설명하세요.', 1),
       (2, 'TECH', '데이터베이스 인덱스의 동작 원리와 주의할 점을 설명하세요.', 1),
       (3, 'INTRO', '1분 자기소개를 해주세요.', 1),
       (4, 'MOTIVATION', '이 직무에 지원한 동기를 말씀해 주세요.', 1),
       (5, 'TECH', '프로세스와 스레드의 차이, 컨텍스트 스위칭 비용에 대해 설명하세요.', 1);

/* QUESTION_JOBS (5개 이상 가능하지만 최소 5행) */
INSERT INTO question_jobs (question_id, job_id)
VALUES

-- Q1: 백엔드/프론트
(1, 1),
(1, 2),
-- Q2: 데이터 직군에 밀접
(2, 3),
-- Q3/Q4: 전 직군 공통
(3, 1),
(3, 2),
(3, 3),
(3, 4),
(3, 5),
(4, 1),
(4, 2),
(4, 3),
(4, 4),
(4, 5),
-- Q5: 개발 직군
(5, 1),
(5, 2);

/* USER FLOW PROGRESS (5) */
INSERT INTO user_flow_progress (user_id, next_phase)
VALUES (1, 'MOTIVATION'),
       (2, 'TECH1'),
       (3, 'INTRO'),
       (4, 'TECH2'),
       (5, 'PERSONALITY');

/* ANSWERS (5) */
INSERT INTO answers (answer_id, user_id, question_id, answer_text, level, starred, created_at)
VALUES (1, 1, 1, 'TLS는 대칭키 교환을 위해 비대칭키를 활용하며...', 4, 0, '2025-09-01 09:12:00'),
       (2, 2, 2, '이것은 답변이다.', 5, 1, '2025-09-02 08:45:00'),
       (3, 3, 3, '안녕하세요, 데이터에 진심인 장효석입니다...', 3, 0, '2025-09-03 21:10:00'),
       (4, 4, 4, '사용자 문제를 정의하고 솔루션을 실험하는 과정이 즐겁습니다.', 4, 1, '2025-09-04 20:05:00'),
       (5, 5, 5, 'answer', NULL, 0, '2025-09-05 07:50:00');

/* ANSWER FEEDBACKS (5) */
INSERT INTO feedbacks (feedback_id, answer_id, status, content, latency_ms, created_at)
VALUES (1, 1, 'DONE', '핵심 단계(TCP 핸드셰이크 vs TLS 핸드셰이크) 구분이 명확합니다. 예시 하나만 더 추가하세요.', 820,
        '2025-09-01 09:13:00'),
       (2, 2, 'DONE', '클러스터드/넌클러스터드 인덱스 차이를 짚은 점이 좋습니다. B+트리 재밸런싱 언급 추가 추천.', 1040,
        '2025-09-02 08:47:00'),
       (3, 3, 'DONE', 'FEEDBACK1', 100, '2025-09-03 21:11:00'),
       (4, 4, 'DONE', 'FEEDBACK2', 230, '2025-09-04 20:06:00'),
       (5, 5, 'DONE', '컨텍스트 스위칭 비용의 원인(CPU 캐시 미스 등) 예시가 좋아요.', 900, '2025-09-05 07:52:00');

/* ----- 마지막으로 USER_PREFERENCES에 jobs FK 연결 보장 ----- */
ALTER TABLE user_preferences
    ADD CONSTRAINT fk_user_prefs_job
        FOREIGN KEY (user_job) REFERENCES jobs (job_id) ON DELETE RESTRICT;
