-- DATA seed. Run after data_jobs.sql.
SET @DATA_ID := (SELECT j_id FROM jobs WHERE name='DATA');

-- FLOW INTRO
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'1분 자기소개를 해주세요.', 'NON_TECH','INTRO','INTRO'),
(@DATA_ID,'데이터 분야에 관심을 갖게 된 계기.', 'NON_TECH','INTRO','INTRO'),
(@DATA_ID,'최근 가장 몰입했던 경험은?', 'NON_TECH','INTRO','INTRO'),
(@DATA_ID,'본인의 강점을 한 가지로 소개해주세요.', 'NON_TECH','INTRO','INTRO'),
(@DATA_ID,'최근 학습 요약을 1분으로 설명해보세요.', 'NON_TECH','INTRO','INTRO');

-- FLOW MOTIVATION
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'데이터 직무를 선택한 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@DATA_ID,'우리 회사 데이터 문제에 관심을 갖는 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@DATA_ID,'3년 후 성장 계획은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@DATA_ID,'최근 데이터/AI 동향 중 인상적인 점은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@DATA_ID,'지원 동기를 말씀해주세요.', 'NON_TECH','MOTIVATION','MOTIVATION');

-- FLOW TECH1 (기초)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'정규화/역정규화 개념.', 'TECH','TECH1','DB'),
(@DATA_ID,'SQL Join 종류와 차이.', 'TECH','TECH1','DB'),
(@DATA_ID,'통계적 가설검정의 개요.', 'TECH','TECH1','STAT'),
(@DATA_ID,'편향과 분산의 트레이드오프.', 'TECH','TECH1','STAT'),
(@DATA_ID,'ETL과 ELT 차이.', 'TECH','TECH1','ETL');

-- FLOW TECH2 (실무)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'데이터 파이프라인 설계 포인트.', 'TECH','TECH2','PIPELINE'),
(@DATA_ID,'Spark와 분산 처리 기본.', 'TECH','TECH2','BIGDATA'),
(@DATA_ID,'특징 공학(feature engineering) 경험.', 'TECH','TECH2','ML'),
(@DATA_ID,'모델 모니터링/드리프트 대응.', 'TECH','TECH2','ML'),
(@DATA_ID,'데이터 품질 관리 전략.', 'TECH','TECH2','DQ');

-- FLOW PERSONALITY
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'이해관계자와의 커뮤니케이션 경험.', 'NON_TECH','PERSONALITY','CULTURE'),
(@DATA_ID,'실패에서 배운 점.', 'NON_TECH','PERSONALITY','CULTURE'),
(@DATA_ID,'우선순위 충돌 조정 경험.', 'NON_TECH','PERSONALITY','CULTURE'),
(@DATA_ID,'피드백 수용 및 개선 사례.', 'NON_TECH','PERSONALITY','CULTURE'),
(@DATA_ID,'압박 상황 해결 경험.', 'NON_TECH','PERSONALITY','CULTURE');

-- TECH Pool (>=30)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@DATA_ID,'윈도 함수(window function) 활용.', 'TECH', NULL, 'SQL'),
(@DATA_ID,'인덱스 설계 포인트.', 'TECH', NULL, 'DB'),
(@DATA_ID,'조인 최적화 전략.', 'TECH', NULL, 'DB'),
(@DATA_ID,'정규분포/중심극한정리 개요.', 'TECH', NULL, 'STAT'),
(@DATA_ID,'로지스틱 회귀의 해석.', 'TECH', NULL, 'ML'),
(@DATA_ID,'정규화/표준화 차이.', 'TECH', NULL, 'ML'),
(@DATA_ID,'ROC-AUC/PR-AUC 차이.', 'TECH', NULL, 'ML'),
(@DATA_ID,'k-Fold 교차검증.', 'TECH', NULL, 'ML'),
(@DATA_ID,'특징 스케일링 필요성.', 'TECH', NULL, 'ML'),
(@DATA_ID,'Spark RDD/DataFrame 차이.', 'TECH', NULL, 'BIGDATA'),
(@DATA_ID,'파티셔닝/버킷팅.', 'TECH', NULL, 'BIGDATA'),
(@DATA_ID,'S3/BigQuery/Redshift 비교.', 'TECH', NULL, 'DW'),
(@DATA_ID,'Airflow DAG 설계.', 'TECH', NULL, 'PIPELINE'),
(@DATA_ID,'데이터 카탈로그의 역할.', 'TECH', NULL, 'DQ'),
(@DATA_ID,'Feature Store 개념.', 'TECH', NULL, 'ML'),
(@DATA_ID,'ML 파이프라인 배포 전략.', 'TECH', NULL, 'ML'),
(@DATA_ID,'모델 서빙/스케일링.', 'TECH', NULL, 'ML'),
(@DATA_ID,'데이터 보안/거버넌스.', 'TECH', NULL, 'SEC'),
(@DATA_ID,'GDPR/개인정보 이슈.', 'TECH', NULL, 'SEC'),
(@DATA_ID,'데이터 수집 설계(이벤트).', 'TECH', NULL, 'COLLECT'),
(@DATA_ID,'A/B 테스트 설계.', 'TECH', NULL, 'EXPERIMENT'),
(@DATA_ID,'실험의 유의성/효과크기.', 'TECH', NULL, 'EXPERIMENT'),
(@DATA_ID,'결측치 처리 전략.', 'TECH', NULL, 'ML'),
(@DATA_ID,'이상치 탐지 방법.', 'TECH', NULL, 'ML'),
(@DATA_ID,'피처 중요도 해석.', 'TECH', NULL, 'ML'),
(@DATA_ID,'Shapley/SHAP 개요.', 'TECH', NULL, 'ML'),
(@DATA_ID,'모델 선택/튜닝 전략.', 'TECH', NULL, 'ML'),
(@DATA_ID,'데이터 파이프라인 장애 대응.', 'TECH', NULL, 'OPS'),
(@DATA_ID,'모니터링/알림 체계.', 'TECH', NULL, 'OBS'),
(@DATA_ID,'원천/중간/서빙 레이어 구분.', 'TECH', NULL, 'DW');


