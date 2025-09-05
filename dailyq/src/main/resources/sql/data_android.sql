-- ANDROID seed. Run after data_jobs.sql.
SET @AND_ID := (SELECT j_id FROM jobs WHERE name='ANDROID');

-- FLOW INTRO
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'1분 자기소개를 해주세요.', 'NON_TECH','INTRO','INTRO'),
(@AND_ID,'안드로이드 개발에 관심을 갖게 된 계기는?', 'NON_TECH','INTRO','INTRO'),
(@AND_ID,'최근 가장 몰입했던 경험은?', 'NON_TECH','INTRO','INTRO'),
(@AND_ID,'본인의 강점을 한 가지로 소개해주세요.', 'NON_TECH','INTRO','INTRO'),
(@AND_ID,'최근 학습 요약을 1분으로 설명해보세요.', 'NON_TECH','INTRO','INTRO');

-- FLOW MOTIVATION
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'우리 회사 안드로이드 앱에 흥미를 느낀 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@AND_ID,'안드로이드 생태계에서 매력적인 점은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@AND_ID,'3년 후 성장 계획은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@AND_ID,'최근 안드로이드 기술 변화 중 인상적이었던 점은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@AND_ID,'지원 동기를 말씀해주세요.', 'NON_TECH','MOTIVATION','MOTIVATION');

-- FLOW TECH1 (CS/플랫폼 기초)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'Activity와 Fragment의 차이.', 'TECH','TECH1','ANDROID'),
(@AND_ID,'Lifecycle과 상태 전이.', 'TECH','TECH1','ANDROID'),
(@AND_ID,'Kotlin Coroutines 기초.', 'TECH','TECH1','KOTLIN'),
(@AND_ID,'RecyclerView 성능 고려사항.', 'TECH','TECH1','UI'),
(@AND_ID,'HTTP/REST 기본과 OkHttp.', 'TECH','TECH1','NETWORK');

-- FLOW TECH2 (실무)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'MVVM + Jetpack 구성.', 'TECH','TECH2','ARCH'),
(@AND_ID,'Flow/LiveData 비교.', 'TECH','TECH2','CONCURRENCY'),
(@AND_ID,'DI(Hilt/Koin) 활용.', 'TECH','TECH2','ARCH'),
(@AND_ID,'메모리/성능 프로파일링.', 'TECH','TECH2','PERF'),
(@AND_ID,'릴리즈 파이프라인/버전 관리.', 'TECH','TECH2','DEPLOY');

-- FLOW PERSONALITY
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'협업 중 갈등 해결 경험.', 'NON_TECH','PERSONALITY','CULTURE'),
(@AND_ID,'실패에서 배운 점.', 'NON_TECH','PERSONALITY','CULTURE'),
(@AND_ID,'일하는 방식의 장단점.', 'NON_TECH','PERSONALITY','CULTURE'),
(@AND_ID,'피드백 수용 및 개선 사례.', 'NON_TECH','PERSONALITY','CULTURE'),
(@AND_ID,'압박 상황에서 해결한 사례.', 'NON_TECH','PERSONALITY','CULTURE');

-- TECH Pool (>=30)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@AND_ID,'Kotlin의 null-safety.', 'TECH', NULL, 'KOTLIN'),
(@AND_ID,'Sealed class 활용.', 'TECH', NULL, 'KOTLIN'),
(@AND_ID,'Coroutine Scope와 취소.', 'TECH', NULL, 'CONCURRENCY'),
(@AND_ID,'Room DB 기본 개념.', 'TECH', NULL, 'DATA'),
(@AND_ID,'WorkManager 사용 사례.', 'TECH', NULL, 'SYSTEM'),
(@AND_ID,'Navigation Component 구조.', 'TECH', NULL, 'UI'),
(@AND_ID,'DataStore vs SharedPreferences.', 'TECH', NULL, 'DATA'),
(@AND_ID,'Retrofit 설계와 에러 처리.', 'TECH', NULL, 'NETWORK'),
(@AND_ID,'OkHttp Interceptor 활용.', 'TECH', NULL, 'NETWORK'),
(@AND_ID,'Jetpack Compose 기초.', 'TECH', NULL, 'UI'),
(@AND_ID,'Compose 성능 최적화.', 'TECH', NULL, 'PERF'),
(@AND_ID,'모듈화/멀티모듈 전략.', 'TECH', NULL, 'ARCH'),
(@AND_ID,'Proguard/R8 설정 포인트.', 'TECH', NULL, 'BUILD'),
(@AND_ID,'앱 용량 최적화.', 'TECH', NULL, 'PERF'),
(@AND_ID,'크래시 분석과 대응.', 'TECH', NULL, 'OBS'),
(@AND_ID,'푸시 알림(Firebase) 구성.', 'TECH', NULL, 'NETWORK'),
(@AND_ID,'오프라인 대응 및 동기화.', 'TECH', NULL, 'SYNC'),
(@AND_ID,'이미지 로딩(Glide/Coil) 전략.', 'TECH', NULL, 'PERF'),
(@AND_ID,'DI와 테스트 더블.', 'TECH', NULL, 'TEST'),
(@AND_ID,'Instrumentation 테스트.', 'TECH', NULL, 'TEST'),
(@AND_ID,'보안 민감정보 취급.', 'TECH', NULL, 'SEC'),
(@AND_ID,'i18n/l10n 전략.', 'TECH', NULL, 'I18N'),
(@AND_ID,'접근성(A11y) 고려사항.', 'TECH', NULL, 'A11Y'),
(@AND_ID,'롱런 태스크와 배터리 관리.', 'TECH', NULL, 'PERF'),
(@AND_ID,'백그라운드 제한 대응.', 'TECH', NULL, 'SYSTEM'),
(@AND_ID,'네트워크 장애 복구 전략.', 'TECH', NULL, 'NETWORK'),
(@AND_ID,'메트릭 수집/로깅.', 'TECH', NULL, 'OBS'),
(@AND_ID,'릴리즈 트랙/롤아웃 전략.', 'TECH', NULL, 'DEPLOY'),
(@AND_ID,'Play Store 정책 대응.', 'TECH', NULL, 'OPS'),
(@AND_ID,'크로스플랫폼 연동 고려.', 'TECH', NULL, 'ARCH');


