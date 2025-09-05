-- BACKEND seed. Run after data_jobs.sql.
SET @BE_ID := (SELECT j_id FROM jobs WHERE name='BACKEND');

-- FLOW INTRO
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'1분 자기소개를 해주세요.', 'NON_TECH','INTRO','INTRO'),
(@BE_ID,'팀에 기여할 수 있는 강점은?', 'NON_TECH','INTRO','INTRO'),
(@BE_ID,'최근 몰입했던 경험을 소개해주세요.', 'NON_TECH','INTRO','INTRO'),
(@BE_ID,'백엔드 분야에 관심을 갖게 된 계기는?', 'NON_TECH','INTRO','INTRO'),
(@BE_ID,'최근 학습 요약을 1분으로 설명해보세요.', 'NON_TECH','INTRO','INTRO');

-- FLOW MOTIVATION
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'백엔드를 선택한 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@BE_ID,'우리 회사에 지원한 동기를 말해보세요.', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@BE_ID,'서버/인프라에 흥미를 느낀 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@BE_ID,'3년 후 역량 로드맵은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@BE_ID,'백엔드에서 이루고 싶은 목표는?', 'NON_TECH','MOTIVATION','MOTIVATION');

-- FLOW TECH1 (CS/네트워킹)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'TCP와 UDP의 차이를 설명해주세요.', 'TECH','TECH1','NETWORK'),
(@BE_ID,'HTTP 상태코드 3xx/4xx/5xx의 의미.', 'TECH','TECH1','HTTP'),
(@BE_ID,'DB 인덱스 동작 원리를 설명해주세요.', 'TECH','TECH1','DB'),
(@BE_ID,'트랜잭션 ACID는 무엇인가요?', 'TECH','TECH1','DB'),
(@BE_ID,'정규화/역정규화의 트레이드오프.', 'TECH','TECH1','DB');

-- FLOW TECH2 (실무)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'Spring에서 빈 생명주기를 설명해주세요.', 'TECH','TECH2','SPRING'),
(@BE_ID,'JPA N+1 문제와 해결책.', 'TECH','TECH2','JPA'),
(@BE_ID,'대용량 트래픽 대응 전략.', 'TECH','TECH2','SCALE'),
(@BE_ID,'CQRS/이벤트 소싱 이해.', 'TECH','TECH2','ARCH'),
(@BE_ID,'캐시 전략과 일관성 이슈.', 'TECH','TECH2','CACHE');

-- FLOW PERSONALITY
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'서비스 장애를 해결했던 경험은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@BE_ID,'코드 리뷰에서 설득했던 경험은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@BE_ID,'우선순위 충돌을 조정한 경험은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@BE_ID,'실패에서 배운 점을 말해주세요.', 'NON_TECH','PERSONALITY','CULTURE'),
(@BE_ID,'동료 협업에서 중요하게 여기는 것은?', 'NON_TECH','PERSONALITY','CULTURE');

-- TECH Pool (>=30)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@BE_ID,'RDBMS와 NoSQL의 차이와 선택 기준.', 'TECH', NULL, 'DB'),
(@BE_ID,'조인 종류(INNER/LEFT/RIGHT)와 차이.', 'TECH', NULL, 'DB'),
(@BE_ID,'인덱스 설계 시 고려사항.', 'TECH', NULL, 'DB'),
(@BE_ID,'트랜잭션 격리수준의 차이.', 'TECH', NULL, 'DB'),
(@BE_ID,'Deadlock 발생 원인과 대응.', 'TECH', NULL, 'DB'),
(@BE_ID,'HTTP 캐시 전략과 CDN.', 'TECH', NULL, 'HTTP'),
(@BE_ID,'OAuth2/OIDC 기본 흐름.', 'TECH', NULL, 'SEC'),
(@BE_ID,'JWT 장단점과 보안 유의점.', 'TECH', NULL, 'SEC'),
(@BE_ID,'Spring AOP 활용 사례.', 'TECH', NULL, 'SPRING'),
(@BE_ID,'DI/IoC 컨테이너 개념.', 'TECH', NULL, 'SPRING'),
(@BE_ID,'JPA 지연로딩과 즉시로딩 차이.', 'TECH', NULL, 'JPA'),
(@BE_ID,'영속성 컨텍스트와 1차 캐시.', 'TECH', NULL, 'JPA'),
(@BE_ID,'대규모 배포에서 Blue-Green/Canary 차이.', 'TECH', NULL, 'DEPLOY'),
(@BE_ID,'메시지 큐 활용 시 장단점.', 'TECH', NULL, 'MQ'),
(@BE_ID,'Saga 패턴을 설명해주세요.', 'TECH', NULL, 'ARCH'),
(@BE_ID,'Circuit Breaker 동작.', 'TECH', NULL, 'RESILIENCE'),
(@BE_ID,'분산 트레이싱 기초(Trace/Span).', 'TECH', NULL, 'OBS'),
(@BE_ID,'모니터링 지표(RED/USE/Golden Signals).', 'TECH', NULL, 'OBS'),
(@BE_ID,'캐싱 전략(Cache Aside/Write Through/Write Back).', 'TECH', NULL, 'CACHE'),
(@BE_ID,'분산 락과 일관성.', 'TECH', NULL, 'DIST'),
(@BE_ID,'분할(sharding)과 파티셔닝.', 'TECH', NULL, 'SCALE'),
(@BE_ID,'로드 밸런싱 알고리즘.', 'TECH', NULL, 'SCALE'),
(@BE_ID,'Idempotency 보장 기법.', 'TECH', NULL, 'API'),
(@BE_ID,'REST와 gRPC 비교.', 'TECH', NULL, 'API'),
(@BE_ID,'배치 처리 설계 포인트.', 'TECH', NULL, 'BATCH'),
(@BE_ID,'쿼리 최적화 기본 전략.', 'TECH', NULL, 'DB'),
(@BE_ID,'락 종류(낙관/비관)와 활용.', 'TECH', NULL, 'DB'),
(@BE_ID,'메모리/CPU 프로파일링 경험.', 'TECH', NULL, 'PERF'),
(@BE_ID,'쓰레드 풀 튜닝 고려사항.', 'TECH', NULL, 'PERF'),
(@BE_ID,'장애 RCA 작성 포인트.', 'TECH', NULL, 'OPS');


