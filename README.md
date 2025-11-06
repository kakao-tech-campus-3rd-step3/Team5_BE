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


## 6. 기술 스택

### 6.0 전체 아키텍처

<img width="5775" height="2075" alt="아키텍쳐_배경" src="https://github.com/user-attachments/assets/6fdeedfb-350d-46b6-a58e-dc0ae28eca81" />


**주요 컴포넌트:**
- **클라이언트**: Vite React 기반 웹앱 또는 Chrome Extension
- **Spring Boot 서버**: 비즈니스 로직 처리 및 API 제공
- **NCP Object Storage**: 음성 파일 저장소
- **NCP CLOVA STT**: 음성을 텍스트로 변환
- **OpenAI GPT**: 답변에 대한 AI 피드백 생성
- **MySQL**: Answer, Feedback, Question 등 데이터 영구 저장

---

### 6.1 백엔드

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

### 6.2 FrontEnd
- **프레임워크**: React (TypeScript)
- **확장 프로그램**: Chrome Extension

### 6.3 인프라
- **컨테이너화**: Docker, Docker Compose
- **이미지 빌드**: Jib
- **CI/CD**: GitHub Actions
- **배포**: SSH 기반 자동 배포

---

## 7. 프로젝트 구조

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

---

## 8. 개발 환경 설정

### 필수 요구사항
- Java 21 이상
- Gradle 8.11 이상
- Docker 및 Docker Compose
- MySQL 8.0

---

## 9. 기대 효과

- **사용자**: 매일 꾸준한 연습 → 성장 체감 → 동기 유지 → 취업 성공 확률↑
- **운영자**: 데이터 축적(답변/난이도/자소서) → AI 학습 자원 확보 → 서비스 고도화
- **시장 경쟁력**: 기존 모의면접 대비 가볍고, 메일 기반 대비 강제성이 있는 '데일리 루틴' 차별화

---

## 10. 추후 로드맵

- **멀티모달 피드백**
    - 현재는 텍스트 위주의 피드백 → **톤 분석**까지 확장
    - 음성 데이터를 기반으로 **태도·목소리 안정성**까지 분석
- **자소서 기반 맞춤 질문 생성**
    - 사용자 자소서 업로드 → N개 핵심 문장 추출 → 직군/기업 컨텍스트로 질문 생성
