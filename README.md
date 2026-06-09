# munglog

## 프로젝트 소개

**munglog**는 반려견 보호자가 자신의 반려견 프로필을 등록하고, 사진과 함께 일기를 작성하며, 같은 지역의 보호자들과 게시글을 통해 정보를 나눌 수 있는 반려견 기록 커뮤니티 웹 서비스입니다.

반려견의 일상을 가볍게 기록하는 개인 로그 기능과, 지역 기반 커뮤니티 기능을 함께 제공하는 것을 목표로 합니다.

## 기획 의도

반려견을 키우는 보호자들은 산책, 병원, 미용, 용품, 일상 기록 등 다양한 정보를 꾸준히 관리하게 됩니다.

munglog는 이러한 기록을 한 곳에 모으고, 같은 지역의 보호자들과 필요한 정보를 나눌 수 있도록 돕는 서비스입니다.

## 1차 필수 기능

- 회원가입
- 로그인
- 반려견 프로필 등록
- 반려견 일기 작성
- 일기 이미지 최대 5장 업로드
- 업로드 전 이미지 미리보기
- 일기 목록 / 상세 조회
- 지역 게시판 조회 / 작성
- 댓글 작성

## 이미지 기능 1차 범위

1차 구현에서는 일기 이미지 기능을 아래 범위로 제한합니다.

- 일기당 이미지 최대 5장 업로드
- 업로드 전 이미지 미리보기
- 일기 상세 화면에서 이미지 목록 조회
- 첫 번째 업로드 이미지를 대표 이미지로 자동 사용

## 추후 확장 예정 기능

- 이미지 개별 삭제
- 대표 이미지 직접 선택
- 이미지 순서 변경
- 일기 수정 시 기존 이미지 유지 / 삭제 / 추가 처리
- 앱 확장
- 채팅
- 공동구매
- 지도 API
- 실시간 알림
- 동영상 업로드

## 기술 스택 예정

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- MySQL 또는 PostgreSQL

### Frontend

- React
- Vite
- React Router
- Axios

### Tools

- IntelliJ IDEA
- Git
- GitHub

## 주요 도메인

- User
- Dog
- Diary
- DiaryImage
- Post
- Comment

## 예상 ERD 구조

- User 1:N Dog
- Dog 1:N Diary
- Diary 1:N DiaryImage
- User 1:N Post
- Post 1:N Comment
- User 1:N Comment

## 예상 API

### Auth

| Method | URL | 설명 |
|---|---|---|
| POST | /api/auth/signup | 회원가입 |
| POST | /api/auth/login | 로그인 |

### Dog

| Method | URL | 설명 |
|---|---|---|
| GET | /api/dogs | 내 반려견 목록 조회 |
| POST | /api/dogs | 반려견 등록 |
| GET | /api/dogs/{dogId} | 반려견 상세 조회 |
| PUT | /api/dogs/{dogId} | 반려견 정보 수정 |
| DELETE | /api/dogs/{dogId} | 반려견 삭제 |

### Diary

| Method | URL | 설명 |
|---|---|---|
| GET | /api/diaries | 일기 목록 조회 |
| POST | /api/diaries | 일기 작성 |
| GET | /api/diaries/{diaryId} | 일기 상세 조회 |
| PUT | /api/diaries/{diaryId} | 일기 수정 |
| DELETE | /api/diaries/{diaryId} | 일기 삭제 |

### Post

| Method | URL | 설명 |
|---|---|---|
| GET | /api/posts | 게시글 목록 조회 |
| POST | /api/posts | 게시글 작성 |
| GET | /api/posts/{postId} | 게시글 상세 조회 |
| PUT | /api/posts/{postId} | 게시글 수정 |
| DELETE | /api/posts/{postId} | 게시글 삭제 |

### Comment

| Method | URL | 설명 |
|---|---|---|
| POST | /api/posts/{postId}/comments | 댓글 작성 |
| DELETE | /api/comments/{commentId} | 댓글 삭제 |

## 1차 개발 목표

1차 개발에서는 모든 기능을 완벽하게 확장하기보다, 아래 흐름이 정상적으로 동작하는 것을 목표로 합니다.

1. 회원가입 / 로그인
2. 반려견 프로필 등록
3. 반려견 일기 작성
4. 이미지 최대 5장 업로드
5. 일기 목록 및 상세 조회
6. 지역 게시판 작성 및 조회
7. 댓글 작성

## 이번 버전에서 제외하는 기능

아래 기능은 프로젝트 범위가 커지는 것을 방지하기 위해 1차 버전에서는 제외합니다.

- 앱
- 채팅
- 결제
- 공동구매
- 지도 API
- 실시간 알림
- 이미지 순서 변경
- 대표 이미지 직접 선택
- 동영상 업로드
- AI 기반 일기 분석 및 성향 맞춤 메이트 추천 기능

## 프로젝트 진행 상태

- [x] 프로젝트 기획
- [x] 기능정의서 작성
- [ ] ERD 설계
- [ ] API 명세 작성
- [ ] Backend 개발
- [ ] Frontend 개발
- [ ] 화면 캡처 정리
- [ ] 트러블슈팅 정리
