# K-Word Pocket

> 인터넷 연결이 불안정하거나 스마트 기기 사용이 낯선 분들을 위한 **포켓형 한국어 단어장** 백엔드 서비스

국내에 갓 입국한 외국인, 고령자, 아동처럼 디지털 환경이 익숙하지 않은 사용자가 식당·길찾기·비상상황에서 필요한 한국어 표현을 복잡한 절차 없이 저장하고 반복 학습할 수 있도록 만든 REST API 서버입니다.

---

## 목차

- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [도메인 모델](#도메인-모델)
- [시작하기](#시작하기)
- [API 명세](#api-명세)
- [인증 방식](#인증-방식)
- [에러 응답](#에러-응답)
- [프로젝트 구조](#프로젝트-구조)
- [테스트](#테스트)
- [협업 방식](#협업-방식)
- [팀](#팀)

---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 (Spring MVC) |
| 인증/인가 | Spring Security 7, JJWT 0.13 (JWT) |
| 데이터 | Spring Data JPA (Hibernate 7) |
| DB | H2 (로컬 개발) · MySQL (운영 예정) |
| 검증 | Jakarta Bean Validation |
| 빌드 | Gradle 9.5.1 |
| 기타 | Lombok |

---

## 주요 기능

### 단어장 & 플래시카드 (Deck / Card)
주제별 단어장을 만들고 그 안에 **앞면(한국어) · 뒷면(의미)** 구조의 플래시카드를 등록·수정·삭제합니다. 단어장은 소유자에게 귀속됩니다.

### 인증 / 회원 (Auth / User)
BCrypt로 비밀번호를 암호화해 저장하고, 로그인 시 JWT Access Token을 발급하는 **Stateless 인증**을 사용합니다. 마이페이지 조회, 비밀번호 변경, 회원 탈퇴를 지원합니다.

### 공지사항 (Notice)
최신순 페이징 목록과 단건 조회를 제공하며, 등록·수정·삭제가 가능합니다. 수정은 JPA Dirty Checking으로 처리합니다.

### FAQ
**비로그인 사용자도 조회 가능한 공개 API**와, 관리자(`ROLE_ADMIN`)만 접근할 수 있는 등록·수정·삭제 API를 컨트롤러 단위로 분리해 운영합니다.

### Q&A (Question / Answer)
사용자가 질문을 올리고 다른 사용자가 답변을 다는 계층형 게시판입니다. 작성자는 요청 body가 아니라 **JWT 인증 정보에서 결정**되며, 수정은 작성자 본인만, 삭제는 작성자 본인 또는 관리자가 할 수 있습니다.

---

## 도메인 모델

```mermaid
erDiagram
    USER ||--o{ DECK : "소유"
    USER ||--o{ QUESTION : "작성"
    USER ||--o{ ANSWER : "작성"
    DECK ||--o{ CARD : "포함"
    QUESTION ||--o{ ANSWER : "달림"

    USER {
        bigint id PK
        varchar email UK "NOT NULL"
        varchar password "NOT NULL, BCrypt"
        varchar role "ROLE_USER | ROLE_ADMIN"
    }
    DECK {
        bigint id PK
        bigint user_id FK "소유자"
        varchar name "NOT NULL"
    }
    CARD {
        bigint id PK
        bigint deck_id FK "NOT NULL"
        varchar front "NOT NULL, 단어"
        varchar back "NOT NULL, 뜻"
    }
    QUESTION {
        bigint id PK
        bigint user_id FK "NOT NULL, 작성자"
        varchar title "NOT NULL, 200자"
        text content "NOT NULL"
    }
    ANSWER {
        bigint id PK
        bigint question_id FK "NOT NULL, 대상 질문"
        bigint user_id FK "NOT NULL, 작성자"
        text content "NOT NULL"
    }
    FAQ {
        bigint id PK
        varchar question "NOT NULL, 200자"
        text answer "NOT NULL"
    }
    NOTICE {
        bigint id PK
        varchar title "NOT NULL"
        text content "NOT NULL"
    }
```

**설계 포인트**

- 모든 엔티티는 공통 `BaseEntity`를 상속해 `createdAt` / `updatedAt`이 JPA Auditing으로 자동 기록됩니다.
- `ANSWER`는 **외래키를 2개 갖는 유일한 테이블**입니다. `question_id`(어느 질문에 달렸나)와 `user_id`(누가 썼나)는 서로 독립적인 1:N 관계로, A가 올린 질문에 B가 답변하는 구조가 자연스럽게 표현됩니다.
- 질문을 삭제하면 그 질문에 달린 답변도 함께 삭제됩니다(벌크 삭제 처리).

---

## 시작하기

### 요구 사항

- JDK 21 이상
- (선택) MySQL 8 — 기본은 H2 인메모리라 별도 설치 없이 실행됩니다

### 실행

```bash
git clone https://github.com/inflearnTeam/k-word-pocket.git
cd k-word-pocket
./gradlew bootRun
```

기본 포트는 `8080`이며, H2 콘솔은 `http://localhost:8080/h2-console`에서 접속할 수 있습니다.

### 환경 설정

`src/main/resources/application.properties`에서 설정합니다.

```properties
# Datasource — 로컬 개발용 H2 인메모리
spring.datasource.url=jdbc:h2:mem:kwordpocket;MODE=MySQL
spring.jpa.hibernate.ddl-auto=create-drop

# JWT 서명 키 (Base64 인코딩, HS384 기준 48바이트 이상)
jwt.secret=${JWT_SECRET}
```

> ⚠️ **JWT 서명 키는 저장소에 커밋하지 마세요.** 유출되면 임의의 사용자·관리자 토큰을 위조할 수 있습니다. 환경변수로 주입하는 것을 권장합니다.
>
> ```bash
> export JWT_SECRET=$(head -c 48 /dev/urandom | base64)
> ./gradlew bootRun
> ```

`ddl-auto=create-drop`이므로 **앱을 재시작하면 데이터가 초기화됩니다.** 운영 전환 시 datasource 블록과 함께 조정이 필요합니다.

---

## API 명세

`🔓` 인증 불필요 · `🔒` 로그인 필요 · `👑` 관리자 전용

### 인증 (Auth)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| POST | `/auth/signup` | 회원가입 | 🔓 |
| POST | `/auth/signin` | 로그인 — 응답 `Authorization` 헤더로 토큰 발급 | 🔓 |
| POST | `/logout` | 로그아웃 | 🔒 |

### 회원 (User)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/users` | 전체 회원 목록 | 🔒 |
| GET | `/users/me` | 내 정보 조회 | 🔒 |
| PUT | `/users/me/password` | 비밀번호 변경 | 🔒 |
| DELETE | `/users/me` | 회원 탈퇴 — 비밀번호 확인 필요 | 🔒 |

### 단어장 (Deck)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| POST | `/decks` | 단어장 생성 → `201` | 🔒 |
| GET | `/decks` | 단어장 목록 | 🔒 |
| GET | `/decks/{id}` | 단어장 단건 조회 | 🔒 |
| PUT | `/decks/{id}` | 단어장 이름 수정 | 🔒 |
| DELETE | `/decks/{id}` | 단어장 삭제 → `204` | 🔒 |

### 플래시카드 (Card)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| POST | `/cards` | 카드 생성 → `201` | 🔒 소유자 |
| GET | `/cards?deckId={id}` | 특정 단어장의 카드 목록 | 🔒 |
| GET | `/cards/{id}` | 카드 단건 조회 | 🔒 |
| PUT | `/cards/{id}` | 카드 수정 | 🔒 소유자 |
| DELETE | `/cards/{id}` | 카드 삭제 → `204` | 🔒 소유자 |

### 공지사항 (Notice)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/notices` | 공지 목록 — 페이징, 최신순 | 🔒 |
| GET | `/notices/{id}` | 공지 단건 조회 | 🔒 |
| POST | `/notices` | 공지 등록 → `201` | 🔒 |
| PUT | `/notices/{id}` | 공지 수정 | 🔒 |
| DELETE | `/notices/{id}` | 공지 삭제 → `204` | 🔒 |

### FAQ

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/faqs` | FAQ 목록 — 페이징, 등록순 | 🔓 |
| GET | `/faqs/{faqId}` | FAQ 단건 조회 | 🔓 |
| POST | `/admin/faqs` | FAQ 등록 → `201` + `Location` | 👑 |
| PUT | `/admin/faqs/{faqId}` | FAQ 수정 | 👑 |
| DELETE | `/admin/faqs/{faqId}` | FAQ 삭제 → `204` | 👑 |

### Q&A (Question / Answer)

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/questions` | 질문 목록 — 페이징, 최신순 | 🔓 |
| GET | `/questions/{questionId}` | 질문 상세 — 답변 목록 포함 | 🔓 |
| POST | `/questions` | 질문 작성 → `201` + `Location` | 🔒 |
| PUT | `/questions/{questionId}` | 질문 수정 | 🔒 작성자 |
| DELETE | `/questions/{questionId}` | 질문 삭제 → `204` | 🔒 작성자·관리자 |
| GET | `/questions/{questionId}/answers` | 답변 목록 | 🔓 |
| GET | `/questions/{questionId}/answers/{answerId}` | 답변 단건 조회 | 🔓 |
| POST | `/questions/{questionId}/answers` | 답변 작성 → `201` + `Location` | 🔒 |
| PUT | `/questions/{questionId}/answers/{answerId}` | 답변 수정 | 🔒 작성자 |
| DELETE | `/questions/{questionId}/answers/{answerId}` | 답변 삭제 → `204` | 🔒 작성자·관리자 |

### 페이징 파라미터

목록 조회 API는 `page` · `size` · `sort`를 지원합니다.

```
GET /questions?page=0&size=10&sort=id,desc
```

응답은 다음 형태입니다.

```json
{
  "content": [ ... ],
  "page": { "size": 10, "number": 0, "totalElements": 42, "totalPages": 5 }
}
```

> Q&A와 FAQ는 허용된 정렬 기준(`id`, `title`/`question`, `createdAt`, `updatedAt`)만 받습니다. 그 외 값은 `400`으로 거부됩니다.

---

## 인증 방식

JWT 기반 Stateless 인증을 사용합니다. 세션을 만들지 않으며, 요청마다 토큰으로 사용자를 식별합니다.

**1. 회원가입**

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

**2. 로그인 — 응답 헤더에서 토큰 획득**

```bash
curl -i -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

```
HTTP/1.1 200
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**3. 인증이 필요한 요청에 토큰 전달**

```bash
curl -X POST http://localhost:8080/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  -d '{"title":"질문 제목","content":"질문 내용"}'
```

토큰 유효기간은 **1시간**이며, 페이로드에 사용자 ID·이메일·권한이 담깁니다. 게시물 작성자는 요청 body가 아닌 **토큰에서 추출**하므로 타인 명의로 글을 쓸 수 없습니다.

---

## 에러 응답

비즈니스 예외는 `@RestControllerAdvice`에서 일괄 처리되어 아래 형식으로 반환됩니다.

```json
{ "code": "NOT_FOUND", "message": "질문을 찾을 수 없습니다." }
```

| 상태 | 발생 상황 |
|---|---|
| `400` | 입력값 검증 실패, 허용되지 않은 정렬 기준 |
| `401` | 토큰이 없거나 유효하지 않음 |
| `403` | 권한 없음 (타인 리소스 수정·삭제, 관리자 전용 API 접근) |
| `404` | 대상 리소스를 찾을 수 없음 |

---

## 프로젝트 구조

도메인별 패키지 안에 `controller` / `service` / `repository` / `entity` / `dto` / `exception`을 두는 **도메인형 패키지 구조**입니다.

```
src/main/java/com/example/kwordpocket/
├── auth/            # 회원가입 · 로그인 · JWT 발급
├── user/            # 회원 정보 · 권한(Role)
├── deck/            # 단어장 · 플래시카드
├── notice/          # 공지사항
├── faq/             # FAQ (공개 조회 / 관리자 관리 분리)
├── qna/             # 질문 · 답변
└── common/
    ├── config/      # SecurityConfig, JwtFilter, JwtUtil
    ├── entity/      # BaseEntity (JPA Auditing)
    └── exception/   # CustomException, GlobalExceptionHandler, ErrorResponse
```

---

## 테스트

```bash
./gradlew test
```

Q&A·FAQ 도메인은 `@SpringBootTest` + `MockMvc` 기반 통합 테스트로 **권한 매트릭스, 작성자 위조 차단, 관리자 게이트, 입력 검증**을 검증합니다.

| 테스트 | 범위 |
|---|---|
| `QnaApiTest` | 수정·삭제 권한, 작성자 결정, 입력 검증, 정렬·조회 |
| `FaqApiTest` | 공개 조회, 관리자 권한, 입력 검증 |

---

## 협업 방식

**브랜치 전략** — `feature/*` → `dev` → `main`

기능은 `feature/기능명` 브랜치에서 개발하고 `dev`로 PR을 올립니다. 팀원 상호 리뷰 후 팀장이 최종 승인하면 머지되며, 안정화된 `dev`를 `main`으로 병합합니다.

**PR / 이슈** — `.github/`의 PR·이슈 템플릿을 사용해 작업 내용, 관련 이슈, 체크리스트를 기재합니다.

**커밋 컨벤션**

```
feat:     새로운 기능
fix:      버그 수정
refactor: 리팩터링
test:     테스트 코드
chore:    빌드 · 설정 등 기타
```

**소통** — Notion(아키텍처·엔티티 구조 문서화), Discord(일일 스크럼 및 블로커 공유)

---

## 팀

| 담당 | 이름 | GitHub |
|---|---|---|
| 단어장 · 플래시카드 (Deck/Card) | 최원준 | |
| 인증 · 회원 (Auth/User) | 이명인 | |
| 공지사항 (Notice) | 허동후 | |
| FAQ · Q&A | 박준용 | |

---

## 향후 개선 계획

- **CI/CD** — GitHub Actions 기반 빌드·테스트 자동화
- **API 문서 자동화** — Swagger(SpringDoc) 적용으로 명세 수기 작성 비용 제거
- **코드 품질** — Spotless / SonarQube 도입
- **운영 환경** — MySQL 전환 및 배포 파이프라인 구성
