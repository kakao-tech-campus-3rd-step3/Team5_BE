-- IOS seed. Run after data_jobs.sql.
SET @IOS_ID := (SELECT j_id FROM jobs WHERE name='IOS');

-- FLOW INTRO
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'1분 자기소개를 해주세요.', 'NON_TECH','INTRO','INTRO'),
(@IOS_ID,'iOS 개발에 관심을 갖게 된 계기는?', 'NON_TECH','INTRO','INTRO'),
(@IOS_ID,'최근 가장 몰입했던 경험은?', 'NON_TECH','INTRO','INTRO'),
(@IOS_ID,'본인의 강점을 한 가지로 소개해주세요.', 'NON_TECH','INTRO','INTRO'),
(@IOS_ID,'최근 학습 요약을 1분으로 설명해보세요.', 'NON_TECH','INTRO','INTRO');

-- FLOW MOTIVATION
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'우리 회사 iOS 앱에 흥미를 느낀 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@IOS_ID,'iOS 생태계에서 매력적인 점은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@IOS_ID,'3년 후 성장 계획은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@IOS_ID,'최근 iOS 기술 변화 중 인상적이었던 점은?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@IOS_ID,'지원 동기를 말씀해주세요.', 'NON_TECH','MOTIVATION','MOTIVATION');

-- FLOW TECH1 (CS/플랫폼 기초)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'ARC와 메모리 관리 기초.', 'TECH','TECH1','IOS'),
(@IOS_ID,'Struct와 Class의 차이.', 'TECH','TECH1','SWIFT'),
(@IOS_ID,'Copy-on-Write(COW) 개념.', 'TECH','TECH1','SWIFT'),
(@IOS_ID,'RunLoop의 역할.', 'TECH','TECH1','IOS'),
(@IOS_ID,'URLSession 동작 원리.', 'TECH','TECH1','IOS');

-- FLOW TECH2 (실무)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'MVVM 설계 포인트.', 'TECH','TECH2','ARCH'),
(@IOS_ID,'Combine/async-await 비교.', 'TECH','TECH2','CONCURRENCY'),
(@IOS_ID,'테스트 전략(Unit/UI Test).', 'TECH','TECH2','TEST'),
(@IOS_ID,'퍼포먼스 측정과 개선 경험.', 'TECH','TECH2','PERF'),
(@IOS_ID,'App Store 배포 파이프라인.', 'TECH','TECH2','DEPLOY');

-- FLOW PERSONALITY
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'협업 중 갈등 해결 경험.', 'NON_TECH','PERSONALITY','CULTURE'),
(@IOS_ID,'실패에서 배운 점.', 'NON_TECH','PERSONALITY','CULTURE'),
(@IOS_ID,'일하는 방식의 장단점.', 'NON_TECH','PERSONALITY','CULTURE'),
(@IOS_ID,'피드백을 수용하고 개선한 사례.', 'NON_TECH','PERSONALITY','CULTURE'),
(@IOS_ID,'압박 상황에서 해결한 사례.', 'NON_TECH','PERSONALITY','CULTURE');

-- TECH Pool (>=30)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@IOS_ID,'Swift의 Optional과 언래핑 패턴.', 'TECH', NULL, 'SWIFT'),
(@IOS_ID,'Value Type과 Reference Type의 차이.', 'TECH', NULL, 'SWIFT'),
(@IOS_ID,'메모리 누수 탐지(Leaks/Allocations).', 'TECH', NULL, 'PERF'),
(@IOS_ID,'GCD와 OperationQueue 차이.', 'TECH', NULL, 'CONCURRENCY'),
(@IOS_ID,'Keychain 사용 및 보안 고려.', 'TECH', NULL, 'SEC'),
(@IOS_ID,'CoreData 기본 개념.', 'TECH', NULL, 'DATA'),
(@IOS_ID,'DiffableDataSource 동작.', 'TECH', NULL, 'UI'),
(@IOS_ID,'AutoLayout 성능 이슈 대응.', 'TECH', NULL, 'UI'),
(@IOS_ID,'Static/Dynamic Framework 차이.', 'TECH', NULL, 'BUILD'),
(@IOS_ID,'모듈화 전략과 장점.', 'TECH', NULL, 'ARCH'),
(@IOS_ID,'앱 용량 최적화 전략.', 'TECH', NULL, 'PERF'),
(@IOS_ID,'푸시 알림 구조(APNs).', 'TECH', NULL, 'NETWORK'),
(@IOS_ID,'Background Task 처리.', 'TECH', NULL, 'SYSTEM'),
(@IOS_ID,'App LifeCycle과 상태 전이.', 'TECH', NULL, 'SYSTEM'),
(@IOS_ID,'SceneDelegate 역할.', 'TECH', NULL, 'SYSTEM'),
(@IOS_ID,'UIKit과 SwiftUI 비교.', 'TECH', NULL, 'UI'),
(@IOS_ID,'의존성 관리(SPM/CocoaPods).', 'TECH', NULL, 'BUILD'),
(@IOS_ID,'Crash 분석 방법.', 'TECH', NULL, 'OBS'),
(@IOS_ID,'로깅/모니터링 전략.', 'TECH', NULL, 'OBS'),
(@IOS_ID,'네트워크 계층 설계.', 'TECH', NULL, 'NETWORK'),
(@IOS_ID,'멀티 모듈 네이밍/버저닝.', 'TECH', NULL, 'BUILD'),
(@IOS_ID,'보안 민감정보 보호 전략.', 'TECH', NULL, 'SEC'),
(@IOS_ID,'A/B 테스트와 실험 설계.', 'TECH', NULL, 'DATA'),
(@IOS_ID,'접근성(A11y) 고려사항.', 'TECH', NULL, 'A11Y'),
(@IOS_ID,'로컬라이제이션(i18n) 전략.', 'TECH', NULL, 'I18N'),
(@IOS_ID,'앱 시작 시간 개선 기법.', 'TECH', NULL, 'PERF'),
(@IOS_ID,'앱 아키텍처 레이어 설계.', 'TECH', NULL, 'ARCH'),
(@IOS_ID,'Instrumentation(메트릭) 수집.', 'TECH', NULL, 'OBS'),
(@IOS_ID,'네트워크 장애 대응 전략.', 'TECH', NULL, 'NETWORK'),
(@IOS_ID,'테스트 더블(Mock/Stub/Fake) 활용.', 'TECH', NULL, 'TEST');


