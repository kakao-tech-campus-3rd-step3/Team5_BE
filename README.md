# 👑 DailyQ

## 1. 서비스 개요

- **서비스명**: **DailyQ**
- **목표**: 취업 준비생이 하루 5분, 하루에 하나씩 면접 질문에 답변하면서
    - **꾸준히 연습하는 습관**을 만들고
    - **AI 피드백**을 통해 부족한 점을 보완하며
    - 면접 실력을 끌어올릴 수 있게 함.
- **핵심 가치**: 무겁지 않은 '1일 1문항' 루틴 → 꾸준한 학습 → 성장 체감 → 동기 유지.

---

## 2. 문제 정의

- **취업 준비생의 학습 패턴 문제**
    - 면접 대비를 꾸준히 하고 싶지만, **매일 무엇을 준비해야 할지 체계가 없음**
    - 자기 답변을 기록하거나 피드백 받을 기회가 부족해, **자신의 성장 정도를 측정하기 어려움**
    - 혼자 공부하다 보니 지루해지고, **동기부여가 금방 떨어져 학습을 중단**하는 경우가 많음
- **기존 서비스의 한계**
    - **모의 면접(예: 사람인 AI 면접)**
        - 1회 단가가 높아 자주 이용하기 어렵고,
        - 실제 학습 루틴으로 이어지기보다는 **일회성 체험**에 머무는 경우가 많음
    - **메일 기반 학습(예: 매일메일)**
        - 사용자가 메일을 열어보지 않으면 학습이 끊김 → **사용자 주도형(passive)** 구조라 지속성이 낮음
        - 짧은 팁 위주의 정보 제공이라, **실제 답변 훈련/피드백 루프**가 부족함
- **결과적으로**
    - 취업 준비생은 **꾸준히 연습할 수 있는 가벼운 루틴**과
    - **즉각적인 피드백과 성취감을 제공하는 도구**를 찾지 못해
    - 학습 지속률이 낮고, **실질적인 역량 향상으로 이어지지 못함**

---

## 3. 솔루션

- 브라우저/앱 실행 시 **자동으로 하루 1문항 노출**
- 답변은 **텍스트/음성** 중 선택 가능 → 음성의 경우 **시간 제한 옵션** 제공
- **AI 피드백**으로 답변의 구조·명확성·근거 강화를 지원
- **아카이브/스트릭/라이벌** 기능으로 성취감·경쟁심 제공
- **꼬리 질문 기능**으로 실제 면접과 유사도 확보

---

## 4. 주요 기능

1. **회원/온보딩**
    - 소셜 로그인(구글/카카오)
    - 원하는 직군 선택 → 직군 내 세부직군 다중 선택
    - [기술 질문] / [면접 플로우 질문] 중 선택
        - [면접 플로우 질문] 선택 시: 보편적 면접 시퀀스 기반 질문 제공
    - 답변 방식(텍스트/음성), 음성의 경우 시간 제한 설정
2. **하루 질문 루틴**
    - 메인 페이지 [오늘의 질문] → 답변 입력(타자/음성)
    - 음성 답변은 자동 STT 처리
    - AI 피드백 + 사용자가 난이도 체크
    - 스트릭 증가
3. **아카이브**
    - 내가 푼 답변/피드백 히스토리
    - 즐겨찾기(핀)/난이도 필터
    - 간단한 메모 작성 기능
4. **동기 부여**
    - 스트릭(연속 기록)
    - 라이벌 지정
5. **유료 서비스**
    - (구독) 일일 질문 한도 추가
        - 면접이 임박한 사용자의 편의성 증대


---

## 5. 기술 흐름

### 5.0 회원가입 및 온보딩 플로우

신규 사용자는 소셜 로그인을 통해 간편하게 가입하고, 온보딩 과정을 통해 맞춤형 서비스를 설정합니다.

**주요 단계:**

1. **소셜 로그인 시작**: 사용자가 구글 또는 카카오 로그인 버튼을 클릭합니다.
2. **OAuth2 인증**: Spring Security가 OAuth2 인증 플로우를 시작하고, 사용자를 구글/카카오 인증 페이지로 리다이렉트합니다.
3. **사용자 인증**: 사용자가 구글/카카오에서 로그인하고 정보 제공에 동의합니다.
4. **사용자 정보 조회**: `CustomOAuth2UserService`가 Authorization Code를 Access Token으로 교환하고, 사용자 정보 API를 호출합니다.
5. **사용자 저장/업데이트**: 이메일로 기존 사용자를 조회하고, 없으면 신규 생성, 있으면 이름만 업데이트합니다.
6. **신규 사용자 초기화**: 신규 사용자인 경우 `UserPreferences`와 `UserFlowProgress`를 기본값으로 생성합니다.
7. **JWT 토큰 발급**: `OAuth2AuthenticationSuccessHandler`에서 Access Token과 Refresh Token을 생성합니다.
8. **토큰 저장**: Refresh Token은 HttpOnly 쿠키에 저장되고, Access Token은 URL 파라미터로 프론트엔드에 전달됩니다.
9. **온보딩 진행**: 신규 사용자는 직군 선택, 질문 모드 선택, 답변 방식 설정을 진행합니다.
10. **서비스 이용 시작**: 온보딩 완료 후 메인 페이지에서 오늘의 질문을 받아 답변을 시작할 수 있습니다.

**토큰 갱신 플로우:**
- Access Token 만료 시: `POST /api/token/refresh`로 Refresh Token을 사용해 새로운 Access Token 발급
- Refresh Token은 쿠키에서 자동으로 읽어옴

---

### 5.1 질문 조회 플로우

사용자가 메인 페이지에서 오늘의 질문을 받아오는 과정입니다.

**주요 단계:**

1. **질문 요청**: 클라이언트가 `GET /api/questions/random` 엔드포인트로 랜덤 질문을 요청합니다.
2. **사용자 설정 조회**: 서버가 `UserPreferences`를 조회하여 사용자의 질문 모드(TECH/FLOW), 직군, 시간 제한 등을 확인합니다.
3. **일일 한도 검증**: 오늘 이미 답변한 질문 수를 확인하여 일일 질문 한도를 초과하지 않았는지 검증합니다.
4. **질문 모드별 처리**:
   - **TECH 모드**: 사용자의 직군에 맞는 기술 질문을 랜덤으로 선택합니다. 이미 답변한 질문은 제외됩니다.
   - **FLOW 모드**: 사용자의 현재 면접 단계(INTRO → MOTIVATION → TECH → PERSONALITY)에 맞는 질문을 선택합니다. `UserFlowProgress`를 통해 현재 단계를 추적합니다.
5. **꼬리 질문 우선 처리**: 미답변 꼬리 질문이 있으면 일반 질문보다 우선적으로 제공합니다.
6. **질문 반환**: 선택된 질문과 함께 질문 모드, 현재 단계, 시간 제한 등의 정보를 포함한 `RandomQuestionResponse`를 반환합니다.

---

### 5.2 음성 답변 플로우

음성 답변의 경우 비동기 STT 처리와 실시간 알림을 통해 사용자 경험을 최적화합니다.

**주요 단계:**

1. **음성 녹음**: 사용자가 브라우저에서 음성을 녹음합니다.
2. **Presigned URL 요청**: 클라이언트가 서버에 업로드용 Presigned URL을 요청합니다.
3. **직접 업로드**: 클라이언트가 Presigned URL을 사용해 NCP Object Storage에 음성 파일을 직접 업로드합니다. (서버 부하 감소)
4. **답변 등록**: 음성 파일 URL과 함께 `/api/answers` 엔드포인트로 답변을 등록합니다.
5. **Answer 생성**: 서버에서 Answer 엔티티를 생성하지만, 아직 텍스트는 없고 STT 상태는 `PENDING`입니다.
6. **STT 작업 시작**: `SttTask`를 생성하고 NCP CLOVA STT API를 호출하여 비동기 변환 작업을 시작합니다.
7. **SSE 연결**: 클라이언트가 `/api/sse/connect`로 SSE 연결을 수립하여 실시간 알림을 받을 준비를 합니다.
8. **STT 콜백 처리**: NCP CLOVA가 변환을 완료하면 `/api/stt/callback`으로 콜백을 보냅니다.
9. **텍스트 업데이트**: 콜백에서 받은 텍스트로 Answer를 업데이트하고 `SttCompletedEvent`를 발행합니다.
10. **AI 피드백 생성**: `FeedbackService`가 OpenAI GPT API를 호출하여 피드백을 생성합니다. (비동기)
11. **실시간 알림**: SSE를 통해 클라이언트에 `stt_completed`, `feedback_ready` 이벤트를 전송합니다.
12. **결과 조회**: 클라이언트가 `/api/answers/{id}`로 최종 답변과 피드백을 조회합니다.

**에러 처리:**
- STT 실패 시: `SttFailedEvent` 발행 → SSE로 알림 → 클라이언트가 `/api/answers/{id}/retry-stt`로 재시도 가능
- 피드백 생성 실패 시: Feedback 상태를 `FAILED`로 업데이트 → 재시도 가능

---

### 5.3 텍스트 답변 플로우

텍스트 답변은 더 단순한 동기 플로우를 따릅니다.

**주요 단계:**

1. **텍스트 입력**: 사용자가 브라우저에서 텍스트로 답변을 입력합니다.
2. **답변 등록**: 클라이언트가 `POST /api/answers` 엔드포인트로 답변을 등록합니다. 요청 본문에는 `questionId`, `answerText` 등이 포함됩니다.
3. **Answer 생성**: 서버에서 Answer 엔티티를 생성합니다. 이 시점에 이미 텍스트가 포함되어 있으며, `sttStatus`는 N/A이고 `feedback` 상태는 `PENDING`입니다.
4. **Feedback 생성**: `FeedbackService`가 PENDING 상태의 Feedback을 생성합니다.
5. **응답 반환**: 서버가 `AnswerInfoResponse`를 반환하며, 여기에는 `answerId`와 현재 상태 정보가 포함됩니다.
6. **SSE 연결**: 클라이언트가 `GET /api/sse/connect`로 SSE 연결을 수립하여 실시간 알림을 받을 준비를 합니다.
7. **AI 피드백 생성**: `FeedbackService`가 OpenAI GPT API를 호출하여 피드백을 생성합니다. 이 과정은 비동기로 처리됩니다.
8. **실시간 알림**: 피드백 생성이 완료되면 SSE를 통해 클라이언트에 `feedback_ready` 이벤트를 전송합니다.
9. **결과 조회**: 클라이언트가 `GET /api/answers/{id}`로 최종 답변과 피드백을 조회합니다. 응답에는 `answerText`, `feedback` (AI 피드백), `question` 정보가 포함됩니다.

**음성 답변과의 차이점:**
- STT 단계가 없어 더 빠른 처리
- Answer 생성 시점에 이미 텍스트가 포함됨
- 피드백 생성만 비동기로 처리

---

### 5.4 꼬리 질문 생성 플로우

사용자의 답변을 바탕으로 AI가 추가 질문을 생성하여 실제 면접과 유사한 경험을 제공합니다.

**주요 단계:**

1. **꼬리 질문 생성 요청**: 사용자가 답변에 대한 피드백을 확인한 후, `POST /api/questions/followUp/{answerId}` 엔드포인트로 꼬리 질문 생성을 요청합니다.
2. **답변 및 질문 조회**: 서버가 해당 Answer와 원본 Question 정보를 조회합니다.
3. **AI 질문 생성**: `FollowUpQuestionService`가 OpenAI GPT API를 호출하여 사용자의 답변을 분석하고, 답변을 더 깊이 있게 탐구할 수 있는 꼬리 질문을 생성합니다.
4. **꼬리 질문 저장**: 생성된 꼬리 질문들을 `FollowUpQuestion` 엔티티로 저장하고, 원본 Answer와 연결합니다.
5. **응답 반환**: 생성된 꼬리 질문의 개수를 포함한 `FollowUpGenerationResponse`를 반환합니다.
6. **질문 조회 시 우선 제공**: 이후 사용자가 `GET /api/questions/random`을 호출하면, 미답변 꼬리 질문이 일반 질문보다 우선적으로 제공됩니다.

**꼬리 질문의 특징:**
- 사용자의 답변 내용을 바탕으로 맥락에 맞는 추가 질문 생성
- 실제 면접에서 면접관이 할 수 있는 심화 질문 시뮬레이션
- 답변의 깊이와 완성도를 높이는 데 도움

---

### 5.5 상태 조회 플로우

클라이언트는 주기적으로 또는 SSE 이벤트 수신 후 상태를 확인할 수 있습니다.

**주요 단계:**

1. **상태 조회 요청**: 클라이언트가 `GET /api/answers/{id}/status` 엔드포인트로 답변의 현재 상태를 조회합니다.
2. **상태 응답**: 서버가 `AnswerInfoResponse`를 반환하며, 여기에는 `answerId`, `sttStatus`, `feedbackStatus` 등의 상태 정보가 포함됩니다.

**상태 값:**
- `sttStatus`: `PENDING` → `COMPLETED` / `FAILED`
- `feedbackStatus`: `PENDING` → `PROCESSING` → `DONE` / `FAILED`

---

## 6. 핵심 이슈
<details>
  <summary>SSE + virtual thread</summary>
    
### 문제점
  CLOVA STT 변환은 비동기 작업으로 이 결과를 사용자에게 '실시간'으로 알려주기 위해 SSE를 도입했음.
  SSE는 특성상 각 사용자가 접속해 있는 내내 서버 스레드 1개를 점유.
    
### 실제 상황
  프론트 테스트에서 동시 접속자가 200명만 넘어가도 모든 OS 쓰레드가 고갈되는 문제가 발생하여 서버 전체가 다운되는 현상 발생.

### 해결방안
  Virtual Thread를 도입하여 SSE연결을 유지하더라도 OS 를 점유하지 않고 10~20개의 적은 OS 쓰레드 만으로 n천개의 SSE 동시 연결을 처리하도록 함.

### 기대 효과 및 검증
- 30초간 쓰레드를 강제로 대기시키는 즉, stt api의 긴 대기 시간 시뮬레이션을 위한 테스트용 API를 구현.
- 기존 os 쓰레드의 한계치를 넘기게 3000명이 동시요청하도록 설정하여 테스트.
- 즉 서버가 3000개의 쓰레드가 동시에 블로킹된 상태를 버티도록 구성.
<img width="1492" height="573" alt="image" src="https://github.com/user-attachments/assets/3909c436-d52c-4e3d-afa7-0be03df4d652" />

- 사진(위) virtual thread를 켠 경우 OS 쓰레드 고갈 없이 모두 성공.
- 사진 (아래) 가상유저가 204명에 도달하자마자 OS 쓰레드 고갈로 나머지 요청 모두 실패.
</details>

<details>
    <summary>nGrinder 도입</summary>

### 도입 계기
- FE가 API가 느리다고 리포트하여, BE팀은 정확한 원인(API 문제? DB 문제? 인프라 문제?)을 재현하길 원함.

### 해결책
- Groovy 스크립트 기반 시나리오: VUser(가상 유저)별로 복잡한 로직을 코드로 작성.
- 추적 및 관리 : API 엔드포인트별로 분리.
- 명확한 병목 시각화: VUser 증가에 따른 Error Rate, TPS, Mean Test Time(MTT)을 실시간 그래프로 확인.


### 기대효과
명확한 SLA제공(BE) - 데이터에 기반한 명확한 SLA(Service Level Agreement) 제공.
안티패턴 방지 및 신뢰 구축(FE) - FE팀이 "API가 느릴 것"이라 지레짐작하여 불필요한 클라이언트 사이드 캐싱이나 복잡한 상태 관리를 구현하는 안티패턴을 방지.

</details>

<details>
    <summary>JWT 기반 로그인 인증/인가</summary>

### 도입 계기
- JWT(JSON Web Token) 도입의 주된 이유는 기존 세션(Session) 기반 인증 방식의 한계점을 극복하고, 확장성 및 효율성을 개선하기 위함

### 해결책
- 로그인 성공 시 서버가 비밀키를 사용해 JWT를 발급하고, 클라이언트는 해당 토큰을 저장 후 요청 시 헤더`(Authorization: Bearer <token>)`에 포함.
- 서버는 토큰 검증만 수행하며, 별도의 세션 상태를 유지하지 않음 **(Stateless 인증 구조)**
- 토큰에 사용자 권한(Role) 및 만료 시간(Expiration Time)을 포함하여 **인가(Authorization)** 를 간편하게 처리
- Refresh Token을 활용해 Access Token 재발급 프로세스 구현으로 보안성과 편의성 강화

### 기대효과
- 서버에 세션 저장이 불필요하므로 **확장성과 성능 향상**
- REST API, 모바일, 프론트엔드 등 다양한 클라이언트 환경에서 **일관된 인증 체계** 유지
- 토큰 기반 검증으로 **보안성 강화**(만료 시간, 서명 검증, HTTPS 연동 등)
  
</details>

<details>
    <summary>관리자 페이지 구현</summary>

### 도입 계기
- 시스템 운영 중 직접 DB 접근이나 개발자 의존적 절차로 수행해야 하는 비효율 존재
- 보안·정책·콘텐츠 관리 등 운영자가 직접 제어할 수 있는 인터페이스 부재.

### 해결책
- **JWT 기반 인증/인가** 연동으로 접근 제어 강화
- 회원 조회 및 수정/삭제
  - 이름 및 역할(Role)별 접근 권한 관리 기능 제공 (관리자/구독자/일반사용자)
- 직군/직업 조회 및 추가/삭제
- 질문 조회 및 추가/수정/삭제
  - 질문 내용, 질문 타입(TECH/INTRO/MOTIVATION/PERSONALITY), 연결된 직업 종류, 활성화 여부(활성/비활성) 관리 제공
 
### 기대효과
- 운영자가 직접 관리 가능한 인터페이스 제공으로 **운영 효율성 향상**
- 관리자 권한 분리 및 인증 강화로 **보안 강화**
- 데이터 및 사용자 관리 자동화로 **개발자 의존도 감소**
- 장애 대응 및 정책 변경 속도 향상으로 **운영 민첩성 확보**
</details>

<details>
    <summary>전역 에러 핸들러</summary>

### 도입 계기
- 보일러 플레이트 코드 발생 : API 컨트롤러마다 ``try-catch``문이 반복
- 일관성 없는 에러 응답 : API는 
- 디버깅에 도움이 되지 않는 로그 : log.error("유저를 찾지 못함")은 도움이 되지 않음
### 해결책

### 기대효과
</details>

<details>
    <summary>템플릿 메서드 패턴</summary>

### 문제점
`AnswerCommandService`의 `submitAnswer` 메서드에서 일반 질문과 꼬리 질문 처리가 하나의 메서드에 혼재되어 있었습니다.
- 코드 중복: 공통 로직(답변 객체 생성, 저장, STT 처리)이 반복됨
- 복잡한 분기: if-else로 일반/꼬리 질문을 구분하며 가독성 저하
- 확장성 부족: 새로운 답변 타입 추가 시 메서드 수정 필요
- 단일 책임(SRP) 위반: 하나의 메서드가 여러 타입의 답변 처리 로직을 포함
- 
### 해결책
- 템플릿 메서드 패턴을 적용해 공통 흐름은 추상 클래스에서 정의하고, 차이점은 하위 클래스에서 구현하도록 분리했습니다:

### 기대효과
- 코드 재사용성 향상: 공통 로직을 한 곳에서 관리
- 가독성 개선: 각 핸들러가 자신의 책임만 담당
- 확장성: 새로운 답변 타입 추가 시 핸들러만 추가하면 됨
- 유지보수성: 변경 영향 범위가 명확해짐
- 테스트 용이성: 각 핸들러를 독립적으로 테스트 가능
- 현재 `AnswerCommandService`는 팩토리에서 핸들러를 받아 `handle()`만 호출하면 됩니다.

</details>

<details>
    <summary>cursor 기반 랜덤 질문 </summary>

### 도입 계기
기존 방식(전체 질문 조회 후 랜덤 선택)은 질문 수가 증가할수록 O(n) 메모리 사용과 느린 쿼리 성능 문제가 발생합니다.
특히 사용자가 이미 답변한 질문을 제외하는 조건을 포함할 경우 쿼리 비용이 비약적으로 증가합니다.

### 해결책
MAX ID를 먼저 조회하여 사용 가능한 질문의 ID 범위를 파악
1부터 MAX ID 사이의 랜덤 ID 생성
Cursor 기반 조회(id >= randomId)로 해당 범위에서 첫 번째 질문 선택
인덱스를 활용한 효율적인 쿼리 (ORDER BY id, LIMIT 1)

### 기대효과
O(1) 메모리 사용: 전체 질문을 메모리에 로드하지 않음
빠른 쿼리 성능: 인덱스 기반 범위 스캔으로 O(log n) 시간 복잡도
확장성 향상: 질문 수가 수만 개로 증가해도 일정한 성능 유지
랜덤성 보장: 각 질문이 동일한 확률로 선택됨
</details>

<details>
    <summary>QueryDSL이 아닌 JPA Specification 도입으로 cursor 기반 무한스크롤 구현 + Slice</summary>

### 도입 계기
- 전통적 Pagination(Offset)의 한계: '아카이브' 기능은 사용자의 모든 답변을 조회해야 함.
    - 페이지 번호가 늘어날 수록 OFFSET N개 만큼의 데이터 스캔이 발생하여 조회 성능 저하
    - UX 관점에서 안티패턴이 발생
- 동적 필터 요구 : FE에서 날짜, 직무, 질문 타입, 즐겨찾기, 난이도 등 다양한 조건으로 동적 검색 요구

### 해결책
- JPA Specification : JPA 표준 기능으로 의존성, 빌드, 학습 비용 최소화
    - ``DTO``를 구성하여 ``Predicate``를 조합하는 ``createSpecification`` 메서드를 통해 유지보수 확보
- 무한 스크롤 : Cursor + Slice
    - OFFSET 대신 lastId와 lastCreatedAt을 조합하여 마지막으로 본 데이터 다음 10개를 조회하는 방식 채택
    - Page는 불필요한 COUNT(*) 쿼리를 추가로 실행하여 성능 저하 -> Slice를 통해 COUNT쿼리를 제거하고 hasNext를 FE에게 전달
### 기대효과
- BE 성능 : 데이터가 수만 건이 존재하여도 OFFSET 스캔이 없으므로, 항상 일정하고 빠른 조회 성능 보장
- BE 유지보수 : QueryDSL 도입 대비 학습 비용 낮추기
- FE/UX : 사용자에게 끊임 없는 무한 스크롤 경험 제공

    
</details>

<details>
    <summary>NCP Object Storage 보안 : Pre-Signed URL 도입</summary>

### 문제점
CLOVA STT API는 파일 경로(dataKey)를 인자로 받기 때문에, 음성 파일이 Object Storage에 먼저 업로드되어야 합니다.
⦁   서버 부하: FE → BE → Storage로 파일을 중계하면 백엔드 부하가 심각합니다.
⦁   보안 취약: FE → Storage로 직접 업로드하려면, FE에 Secret Key가 노출되어 치명적입니다.

### 해결책
Pre-signed URL (임시 허가증)
백엔드가 Secret Key를 안전하게 보관하면서, FE에게 "10분 동안, 특정 경로에, PUT 요청만 허용하는" 임시 업로드 URL을 발급합니다.
⦁   FE → BE: 업로드 요청 (/api/answers/upload-url)
⦁   BE → FE: Pre-signed URL 생성 및 반환
⦁   FE → NCP Storage: FE가 이 임시 URL을 사용해 스토리지로 파일을 직접 PUT (업로드)
⦁   (이후) FE → BE: "업로드 완료" 신호와 함께 STT 요청 (이때 BE가 Clova API 호출)

핵심 보안 강화 조치
⦁   사용자별 경로 격리 (User Scoping)
문제: 모든 사용자가 같은 곳에 업로드하면 파일이 덮어써지거나 경로 조작 공격이 가능합니다.
해결: 파일 경로(objectKey)를 **uploads/{userId}/{UUID}.[확장자]**로 강제하여 논리적으로 격리시켰습니다.

⦁   파일 확장자 검증 (Allow-list)
문제: .html, .exe 같은 악성 파일 업로드 시도
해결: Clova가 지원하는 오디오 형식(.mp3, .m4a 등)의 '허용 목록'과 비교 검증합니다. 목록에 없으면 400 Bad Request를 반환하여 업로드를 원천 차단합니다.
</details>

<details>
    <summary>CORS정책 및 Preflight 해결</summary>

### 문제점
- CORS 정책 위반: 브라우저와 서버의 Origin이 달라, 브라우저의 동일 출처 정책(SOP)에 의해 API 요청이 기본적으로 차단됨.
- Preflight (OPTIONS) 요청 발생: 본 요청(POST, PUT 등)에 Authorization 헤더(JWT 토큰)를 포함시킴.
- Authorization 헤더는 Simple Request 조건에 해당하지 않으므로, 브라우저는 본 요청 전 서버의 허용 여부를 묻는 OPTIONS 메서드(Preflight) 요청을 먼저 전송함.
- 브라우저가 보낸 OPTIONS 요청에는 인증 토큰(Authorization 헤더)이 없음.
    - 따라서 JwtAuthenticationFilter 이전에 Spring Security의 인증 체인이 먼저 동작하여, 이 OPTIONS 요청을 401 Unauthorized 또는 403 Forbidden으로 차단함.
- 브라우저는 Preflight 요청이 실패(200 OK가 아님)했으므로, 본 요청(POST 등)을 보내지 않고 CORS 에러를 발생시킴.
### 해결책
- OPTIONS 요청에 대한 Spring Security 인증 해제
    - filterChain 메서드 내에서 authorizeHttpRequests 설정을 통해 모든 OPTIONS 메서드 요청은 인증 절차 없이 통과(permit)시킴.
- 구체적인 CORS 정책 정의 및 적용
    - 허용 출처 (Origins): application.yml에 정의된 프론트엔드 도메인 목록을 허용 
    - 허용 메서드 (Methods): OPTIONS를 포함한 GET, POST, PUT, DELETE 등 모든 메서드 허용 
    - 허용 헤더 (Headers): Authorization 헤더를 포함한 모든 헤더 허용 (setAllowedHeaders(List.of("*"))).
    - 자격 증명 (Credentials): 쿠키(예: OAuth refresh_token)를 주고받을 수 있도록 허용 (setAllowCredentials(true)).
    - filterChain 내에서 .cors(cors -> cors.configurationSource(corsConfigurationSource))를 호출하여, Spring Security가 이 CORS 규칙을 사용하도록 설정함.
### 기대효과
- Preflight 요청 처리: 브라우저가 OPTIONS 요청을 보내면, Spring Security의 permitAll 규칙 덕분에 인증 없이 통과됨.
- 본 요청 처리: 브라우저가 Preflight 성공을 확인하고 Authorization 헤더가 포함된 실제 POST 요청을 전송함.
- 이 요청은 OPTIONS가 아니므로 permitAll 규칙에 걸리지 않고, anyRequest().authenticated() 규칙에 따라 인증이 필요하게 됨.
- JwtAuthenticationFilter가 토큰을 성공적으로 검증하여 인증을 완료시키고, 컨트롤러까지 요청이 정상적으로 도달함.
</details>
  
## 7. 기술 스택

### 7.0 전체 아키텍처

<img width="6513" height="2075" alt="배굥" src="https://github.com/user-attachments/assets/77097b0c-b8a7-4465-b3ea-97ad55bbb62d" />



**주요 컴포넌트:**
- **클라이언트**: Vite React 기반 웹앱 또는 Chrome Extension
- **Spring Boot 서버**: 비즈니스 로직 처리 및 API 제공
- **NCP Object Storage**: 음성 파일 저장소
- **NCP CLOVA STT**: 음성을 텍스트로 변환
- **OpenAI GPT**: 답변에 대한 AI 피드백 생성
- **MySQL**: Answer, Feedback, Question 등 데이터 영구 저장

---

### 7.1 백엔드

| Java v21 | MySQL v8.0 | Spring v3.5.5 | Docker v27.3.1 | H2 v2.2.224 | nGrinder v3.5.9 |
|:---------:|:----------:|:-------------:|:---------------:|:------------:|:----------------:|

<img width="466" height="601" alt="KakaoTalk_Photo_2025-11-06-19-13-00" src="https://github.com/user-attachments/assets/47deca90-9005-4200-80e1-a1ad254e88ba" style="width:400px" />

- **프레임워크**: Spring Boot 3.5.5
- **언어**: Java 21
- **빌드 도구**: Gradle 8.11
- **인증**: Spring Security OAuth2 Client, JWT (jjwt 0.11.5)
- **데이터베이스**: MySQL 8.0 (InnoDB)
- **ORM**: Spring Data JPA
- **AI 연동**: 
  - OpenAI GPT (Spring AI 1.0.0)
  - NCP CLOVA STT (음성→텍스트 변환)
- **스토리지**: NCP Object Storage (AWS S3 호환 API)
- **모니터링**: Spring Actuator, nGrinder 3.5.9
- **문서화**: SpringDoc OpenAPI 2.8.1
- **비동기 통신**: Server-Sent Events (SSE)

### 7.2 FrontEnd
- **프레임워크**: React (TypeScript)
- **확장 프로그램**: Chrome Extension

### 7.3 인프라
- **컨테이너화**: Docker, Docker Compose
- **이미지 빌드**: Jib
- **CI/CD**: GitHub Actions
- **배포**: SSH 기반 자동 배포

---

## 8. 프로젝트 구조

```
dailyq/
├── src/main/java/com/knuissant/dailyq/
│   ├── config/              # 설정 클래스 (Security, OAuth2, JWT 등)
│   ├── controller/          # REST API 컨트롤러
│   ├── domain/              # 도메인 엔티티 (Answer, Question, User 등)
│   ├── dto/                 # 데이터 전송 객체
│   ├── event/               # 이벤트 처리 (STT 완료, 피드백 생성 등)
│   ├── exception/           # 예외 처리 및 에러 코드
│   ├── external/            # 외부 API 연동 (GPT, NCP 등)
│   ├── jwt/                 # JWT 토큰 생성 및 검증
│   ├── repository/          # 데이터 접근 계층 (JPA Repository)
│   └── service/             # 비즈니스 로직 계층
├── src/main/resources/
│   ├── application.yml      # 애플리케이션 설정
│   ├── prompts/             # AI 프롬프트 템플릿
│   └── static/              # SQL 스크립트 (스키마, 목 데이터)
└── build.gradle             # 의존성 관리

```

### E-R 다이어그램
<img width="1130" height="671" alt="스크린샷 2025-11-07 오전 1 37 05" src="https://github.com/user-attachments/assets/3bc8b40f-737b-4b03-adc2-a8a1d646fac5" />

---

## 9. 개발 환경 설정

### 필수 요구사항
- Java 21 이상
- Gradle 8.11 이상
- Docker 및 Docker Compose
- MySQL 8.0

---

## 10. 기대 효과

- **사용자**: 매일 꾸준한 연습 → 성장 체감 → 동기 유지 → 취업 성공 확률↑
- **운영자**: 데이터 축적(답변/난이도/자소서) → AI 학습 자원 확보 → 서비스 고도화
- **시장 경쟁력**: 기존 모의면접 대비 가볍고, 메일 기반 대비 강제성이 있는 '데일리 루틴' 차별화

---

## 11. 추후 로드맵

- **멀티모달 피드백**
    - 현재는 텍스트 위주의 피드백 → **톤 분석**까지 확장
    - 음성 데이터를 기반으로 **태도·목소리 안정성**까지 분석
- **자소서 기반 맞춤 질문 생성**
    - 사용자 자소서 업로드 → N개 핵심 문장 추출 → 직군/기업 컨텍스트로 질문 생성
