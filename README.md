# 🥬 중고 거래 플랫폼 '배추마켓'

Spring Boot, JPA, Spring Security(JWT, OAuth2)를 기반으로 구현한 중고 거래 웹 애플리케이션입니다.


## ✨ 주요 기능

-   **회원 & 인증**
    -   JWT 기반의 API 인증 및 인가
    -   일반 회원가입 및 로그인
    -   Spring Security + OAuth2 Client를 이용한 카카오 소셜 로그인

-   **상품 (CRUD)**
    -   이미지 업로드를 포함한 상품 등록, 조회, 수정, 삭제
    -   메인 페이지에 최신순으로 상품 목록 노출

-   **실시간 채팅**
    -   WebSocket(Stomp) 기반 1:1 실시간 채팅 기능
    * 상품 상세 페이지에서 '판매자와 채팅하기'로 채팅 시작
    -   채팅방 내 대화 중인 상품 정보 표시
    -   채팅 목록 및 마지막 메시지/시간 표시



## 🛠️ 기술 스택 (Tech Stack)

### Backend
-   **Java**
-   **Spring Boot**
-   **Spring Security**: JWT 토큰 기반 인증/인가 및 OAuth2를 이용한 소셜 로그인 처리
-   **Spring Data JPA** & **Hibernate**: 데이터베이스 연동 및 ORM
-   **Gradle**: 의존성 및 빌드 관리

### Frontend
-   **HTML5**
-   **CSS3**
-   **JavaScript**
-   **Thymeleaf**

### Real-time & Database
-   **WebSocket** & **Stomp**: 실시간 채팅 기능 구현
-   **MySQL**: 개발 및 테스트용 인메모리 데이터베이스

<br>

## 🏛️ 아키텍처 및 인증 흐름

### JWT 인증 프로세스
1.  **로그인 요청**: 클라이언트가 아이디/비밀번호 또는 카카오 인증 코드로 로그인 요청
2.  **인증 처리**: Spring Security가 로그인 요청을 받아 사용자 인증 처리
3.  **JWT 발급**: 인증 성공 시, 서버는 Access Token과 Refresh Token을 생성하여 클라이언트에 전달
4.  **API 요청**: 클라이언트는 이후 모든 API 요청 시 Header에 Access Token을 담아 전송
5.  **인가 처리**: 서버는 JWT 필터를 통해 토큰의 유효성을 검증하고, 유효한 경우 요청을 허용
