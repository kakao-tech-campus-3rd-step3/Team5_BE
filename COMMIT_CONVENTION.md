# 📝 커밋 메시지 컨벤션

## 🎯 개요
프로젝트의 일관성과 가독성을 위해 커밋 메시지 작성 규칙을 정의합니다.

## 📋 커밋 타입

### ✨ feat: 새 기능 추가
새로운 기능을 추가할 때 사용합니다.
```bash
git commit -m "feat: 사용자 로그인 기능 추가"
git commit -m "feat: AI 피드백 시스템 구현"
```

### 🐛 fix: 버그 수정
버그를 수정할 때 사용합니다.
```bash
git commit -m "fix: 로그인 시 세션 만료 오류 수정"
git commit -m "fix: 음성 녹음 파일 업로드 실패 문제 해결"
```

### 📚 docs: 문서 수정
문서를 추가하거나 수정할 때 사용합니다.
```bash
git commit -m "docs: API 문서 업데이트"
git commit -m "docs: README 프로젝트 소개 추가"
```

### 💅 style: 포맷팅 변경
코드 포맷팅을 변경할 때 사용합니다. (코드 로직 변경 없음)
```bash
git commit -m "style: 코드 포맷팅 정리"
git commit -m "style: 들여쓰기 통일"
```

### 🔧 refactor: 코드 리팩토링
새 기능도 아니고 버그도 아니지만 코드가 변경되었을 때 사용합니다.
```bash
git commit -m "refactor: 사용자 서비스 클래스 구조 개선"
git commit -m "refactor: 데이터베이스 쿼리 최적화"
```

### 🧪 test: 테스트 관련 코드 추가/수정
테스트 코드를 추가하거나 수정할 때 사용합니다.
```bash
git commit -m "test: 사용자 인증 테스트 케이스 추가"
git commit -m "test: 통합 테스트 환경 설정"
```

### 🚀 chore: 설정/빌드 수정
코드 추가 없이 설정이나 빌드 관련 파일을 수정할 때 사용합니다.
```bash
git commit -m "chore: Gradle 버전 업데이트"
git commit -m "chore: Docker 설정 파일 추가"
```

## 📝 커밋 메시지 작성 규칙

### 1. 기본 형식
```
<type>: <description>

[optional body]

[optional footer]
```

### 2. 제목 작성 규칙
- **50자 이내**로 작성
- **한국어** 사용 (프로젝트 특성상)
- **명령형**으로 작성 (과거형 X)
- **마침표** 사용하지 않음

### 3. 예시
```bash
# ✅ 좋은 예시
git commit -m "feat: 면접 질문 자동 생성 기능 구현"
git commit -m "fix: 음성 녹음 시 브라우저 호환성 문제 해결"
git commit -m "docs: API 엔드포인트 문서 추가"

# ❌ 나쁜 예시
git commit -m "면접 질문 기능 추가"  # 타입 누락
git commit -m "fix: 버그 수정"        # 너무 모호함
git commit -m "feat: Added interview question feature"  # 영어 사용
```

## 🔍 커밋 히스토리 예시

```
feat: 사용자 인증 시스템 구현
├── feat: JWT 토큰 기반 인증 추가
├── feat: 소셜 로그인(구글/카카오) 연동
├── test: 인증 관련 테스트 케이스 작성
└── docs: 인증 API 문서 작성

fix: 음성 녹음 기능 개선
├── fix: 브라우저별 음성 녹음 호환성 문제 해결
├── refactor: 음성 처리 로직 최적화
└── test: 음성 녹음 테스트 코드 추가

chore: 프로젝트 설정 개선
├── chore: Gradle 버전 8.14.3으로 업데이트
├── chore: Docker Compose 설정 추가
└── style: 코드 포맷팅 정리
```

## 💡 팁

1. **작은 단위로 커밋하기**: 한 번에 여러 기능을 넣지 말고 논리적 단위로 분리
2. **명확한 설명**: 무엇을 왜 변경했는지 명확하게 작성
3. **일관성 유지**: 팀원들과 동일한 스타일로 작성
4. **커밋 전 검토**: `git log --oneline`으로 최근 커밋 확인

---

*일관된 커밋 메시지로 프로젝트 히스토리를 깔끔하게 관리하세요! 🚀*
