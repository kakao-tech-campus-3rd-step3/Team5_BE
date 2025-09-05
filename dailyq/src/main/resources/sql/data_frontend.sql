-- FRONTEND seed. Run after data_jobs.sql.
SET @FE_ID := (SELECT j_id FROM jobs WHERE name='FRONTEND');

-- FLOW: INTRO (>=5)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'1분 자기소개를 해주세요.', 'NON_TECH','INTRO','INTRO'),
(@FE_ID,'본인의 강점을 한 가지로 정의한다면?', 'NON_TECH','INTRO','INTRO'),
(@FE_ID,'최근 가장 몰입했던 경험은 무엇인가요?', 'NON_TECH','INTRO','INTRO'),
(@FE_ID,'최근 학습한 내용을 1분으로 요약해 설명해보세요.', 'NON_TECH','INTRO','INTRO'),
(@FE_ID,'팀에 기여할 수 있는 역량 한 가지를 소개해주세요.', 'NON_TECH','INTRO','INTRO');

-- FLOW: MOTIVATION (>=5)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'프론트엔드를 선택한 이유는 무엇인가요?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@FE_ID,'우리 회사/서비스에 지원한 동기는 무엇인가요?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@FE_ID,'최근 프론트엔드 분야에서 흥미로웠던 변화는?', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@FE_ID,'3년 후 본인의 성장 계획을 말해보세요.', 'NON_TECH','MOTIVATION','MOTIVATION'),
(@FE_ID,'이직/지원 타이밍을 결정한 이유는?', 'NON_TECH','MOTIVATION','MOTIVATION');

-- FLOW: TECH1 (>=5)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'시맨틱 태그와 사용 이유를 설명해주세요.', 'TECH','TECH1','CS_WEB'),
(@FE_ID,'var, let, const의 차이와 호이스팅.', 'TECH','TECH1','JS'),
(@FE_ID,'Promise와 async/await 차이.', 'TECH','TECH1','JS'),
(@FE_ID,'브라우저 렌더링 과정을 설명해주세요.', 'TECH','TECH1','WEB'),
(@FE_ID,'RESTful API란 무엇인가요?', 'TECH','TECH1','WEB');

-- FLOW: TECH2 (>=5)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'React의 Virtual DOM은 무엇이며 왜 쓰나요?', 'TECH','TECH2','REACT'),
(@FE_ID,'React 컴포넌트 생명주기를 설명해주세요.', 'TECH','TECH2','REACT'),
(@FE_ID,'useEffect와 의존성 배열의 의미.', 'TECH','TECH2','REACT'),
(@FE_ID,'웹 성능 최적화 경험을 설명해주세요.', 'TECH','TECH2','WEB'),
(@FE_ID,'상태관리 도구 선택 기준과 트레이드오프.', 'TECH','TECH2','STATE');

-- FLOW: PERSONALITY (>=5)
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'의견 충돌을 해결했던 경험은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@FE_ID,'실패/실수를 통해 배운 점은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@FE_ID,'본인이 일하는 방식의 장단점을 말해보세요.', 'NON_TECH','PERSONALITY','CULTURE'),
(@FE_ID,'동료에게 받은 피드백 중 기억에 남는 것은?', 'NON_TECH','PERSONALITY','CULTURE'),
(@FE_ID,'압박 상황에서 문제를 해결한 경험은?', 'NON_TECH','PERSONALITY','CULTURE');

-- TECH Pool (>=30). 아래 30개 예시는 중복 없이 추가.
INSERT INTO questions(j_id, content, type, flow_phase, topic) VALUES
(@FE_ID,'이벤트 루프(Event Loop)를 설명해주세요.', 'TECH', NULL, 'JS'),
(@FE_ID,'클로저(Closure)는 무엇이고 어디에 쓰이나요?', 'TECH', NULL, 'JS'),
(@FE_ID,'Debounce와 Throttle의 차이.', 'TECH', NULL, 'JS'),
(@FE_ID,'CORS가 무엇이며 어떻게 해결하나요?', 'TECH', NULL, 'WEB'),
(@FE_ID,'CSR/SSR/SSG의 차이와 선택 기준.', 'TECH', NULL, 'WEB'),
(@FE_ID,'HTTP/1.1과 HTTP/2의 차이.', 'TECH', NULL, 'WEB'),
(@FE_ID,'브라우저 캐시 전략(Cache-Control, ETag).', 'TECH', NULL, 'WEB'),
(@FE_ID,'Webpack과 Vite의 차이.', 'TECH', NULL, 'BUILD'),
(@FE_ID,'Tree Shaking은 어떻게 동작하나요?', 'TECH', NULL, 'BUILD'),
(@FE_ID,'코드 스플리팅(Code Splitting) 방식.', 'TECH', NULL, 'PERF'),
(@FE_ID,'React Reconciliation 개념.', 'TECH', NULL, 'REACT'),
(@FE_ID,'useMemo/useCallback 최적화 포인트.', 'TECH', NULL, 'REACT'),
(@FE_ID,'Key의 역할과 성능 영향.', 'TECH', NULL, 'REACT'),
(@FE_ID,'CSR에서 SEO 대응 전략.', 'TECH', NULL, 'SEO'),
(@FE_ID,'IntersectionObserver 활용 사례.', 'TECH', NULL, 'WEB'),
(@FE_ID,'Service Worker와 PWA 기본.', 'TECH', NULL, 'PWA'),
(@FE_ID,'이미지 최적화 기법(WebP, AVIF, lazyload).', 'TECH', NULL, 'PERF'),
(@FE_ID,'네트워크 성능 측정 지표(Core Web Vitals).', 'TECH', NULL, 'PERF'),
(@FE_ID,'XSS/CSRF 방어 전략.', 'TECH', NULL, 'SEC'),
(@FE_ID,'SameSite 쿠키 속성 이해.', 'TECH', NULL, 'SEC'),
(@FE_ID,'프론트엔드 모니터링/로깅 방법.', 'TECH', NULL, 'OBS'),
(@FE_ID,'Error Boundary의 역할.', 'TECH', NULL, 'REACT'),
(@FE_ID,'상태관리 라이브러리 비교(Redux/Zustand/Recoil).', 'TECH', NULL, 'STATE'),
(@FE_ID,'WebSocket과 SSE 차이.', 'TECH', NULL, 'REALTIME'),
(@FE_ID,'프론트엔드 테스트 전략(Unit/IT/E2E).', 'TECH', NULL, 'TEST'),
(@FE_ID,'TDD의 장단점과 적용 경험.', 'TECH', NULL, 'TEST'),
(@FE_ID,'CSS-in-JS와 전통 CSS 비교.', 'TECH', NULL, 'STYLE'),
(@FE_ID,'Atomic Design 개념.', 'TECH', NULL, 'UI'),
(@FE_ID,'접근성(A11y) 개선 경험.', 'TECH', NULL, 'A11Y'),
(@FE_ID,'국제화(i18n) 전략.', 'TECH', NULL, 'I18N');


