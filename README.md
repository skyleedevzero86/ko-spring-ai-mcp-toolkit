<img width="826" height="898" alt="image" src="https://github.com/user-attachments/assets/41f58f74-5ef0-44fa-a061-0f363a7d8b0d" />

<br/>

# Spring AI MCP Toolkit

RAG와 네트워크 검색을 활용한 AI 채팅 애플리케이션입니다. <br/>
Spring AI와 MCP를 통합하여 지식베이스 기반 대화와 실시간 네트워크 검색 기능을 제공합니다.

## 주요 기능

- 🤖 **다중 모드 채팅**
  - 직접 대화: GLM-4.7 모델을 사용한 일반 대화
  - 지식베이스: 업로드된 문서를 기반으로 한 RAG 대화
  - 네트워크 검색: SearXNG를 통한 실시간 인터넷 검색 기반 대화

- 📄 **문서 관리**
  - 문서 업로드 및 벡터화
  - Redis 기반 벡터 스토어를 통한 유사도 검색
  - 자동 문서 분할 및 임베딩

- 🔄 **실시간 스트리밍**
  - Server-Sent Events를 통한 실시간 응답 스트리밍
  - 비동기 메시지 처리

- 🔌 **MCP 통합**
  - Spring AI MCP 클라이언트/서버 지원
  - SSE 및 stdio 프로토콜 지원

## 기술 스택

### Backend
- **Spring Boot** 4.0.1
- **Kotlin** 2.2.21
- **Java** 21
- **Spring AI** 2.0.0-M1
- **Redis** (벡터 스토어)
- **Maven**

### Frontend
- **Next.js** 16.1.1
- **React** 19.2.3
- **TypeScript** 5
- **Tailwind CSS** 4
- **pnpm**

## 프로젝트 구조

```
ko-spring-ai-mcp-toolkit/
├── Backend/
│   ├── mcp-server/          # MCP 서버 모듈
│   └── mcp-client/          # MCP 클라이언트 모듈 (RAG, 채팅 기능)
│       └── src/main/kotlin/com/sleekydz86/chat/
│           ├── domain/
│           │   ├── application/    # 애플리케이션 서비스
│           │   ├── controller/     # REST 컨트롤러
│           │   ├── infrastructure/ # 인프라 구현
│           │   └── model/          # 도메인 모델
│           └── global/             # 전역 설정 및 유틸리티
└── frontend/                # Next.js 프론트엔드
    └── app/                 # Next.js App Router
```

## 시작하기

### 사전 요구사항

- Java 21 이상
- Maven 3.6 이상
- Node.js 18 이상
- pnpm 9 이상
- Redis 6 이상


## API 엔드포인트

### 채팅 API

- `POST /chat/send` - 메시지 전송
  ```json
  {
    "currentUserName": "user-id",
    "message": "질문 내용",
    "mode": "DIRECT" | "KNOWLEDGE_BASE" | "INTERNET_SEARCH"
  }
  ```

### SSE API

- `GET /sse/connect?userId={userId}` - SSE 연결

### RAG API

- `POST /rag/upload` - 문서 업로드 (multipart/form-data)
  - 파일 형식: `.txt`, `.pdf`, `.md`, `.docx`

## 채팅 모드

### DIRECT
GLM-4.7 모델을 사용한 일반적인 AI 대화 모드입니다.

### KNOWLEDGE_BASE
업로드된 문서를 벡터 스토어에서 검색하여 관련 정보를 컨텍스트로 활용하는 RAG 모드입니다.

### INTERNET_SEARCH 
SearXNG를 통해 실시간으로 인터넷을 검색하고, 검색 결과를 기반으로 답변을 생성합니다.


## 라이선스

Apache License 2.0

## 기여

이슈 및 풀 리퀘스트를 환영합니다. 프로젝트를 포크하고 변경사항을 커밋한 후 풀 리퀘스트를 제출해주세요.

## 문제 해결

### Redis 연결 오류
- Redis 서버가 실행 중인지 확인하세요
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` 환경 변수를 확인하세요

### API 키 오류
- `GLM47_API_KEY` 환경 변수가 올바르게 설정되었는지 확인하세요
- API 키가 유효한지 확인하세요

### 포트 충돌
- 기본 포트: MCP Server (6080), MCP Client (8080), Frontend (3000)
- 포트가 사용 중이면 환경 변수로 변경하세요



[**읽기만 하는 AI는 그만! Spring AI와 MCP로 파일 조작부터 메일 발송까지 실현하기**](https://velog.io/@sleekydevzero86/spring-ai-mcp-tool-calling)

