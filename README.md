# munglog 🐾

## 프로젝트 소개

**munglog**는 반려견 보호자가 반려견의 일상을 기록하고, 지역 기반의 피드 기능을 통해 같은 지역 보호자들과 강아지 일상을 공유하며 소통할 수 있는 반려견 블로그 커뮤니티 서비스입니다.

개인적인 강아지 기록을 위한 '일기장' 기능과, 공개 설정을 통해 이웃과 소통하는 '동네 피드' 기능을 하나의 흐름으로 통합하여 제공합니다.

## 기획 의도

반려견을 키우는 보호자들은 산책, 병원, 미용, 용품, 일상 기록 등 다양한 정보를 꾸준히 관리하게 됩니다.
munglog는 이러한 기록을 한 곳에 모으고, 같은 지역의 보호자들과 필요한 정보를 나눌 수 있도록 돕는 서비스입니다.

## 1차 필수 기능

- 회원가입 / 로그인 / 로그아웃
- 반려견 프로필 등록 및 관리
- 반려견 일기 작성 (전체 공개 / 비공개 및 댓글 허용 설정)
- 일기 이미지 최대 5장 업로드 및 미리보기
- 내 반려견의 일기 목록 / 상세 조회
- 지역 기반 동네 피드 조회 (최신순)
- 일기 댓글 작성 및 삭제

## 이미지 기능 1차 범위

1차 구현에서는 일기 이미지 기능을 아래 범위로 제한하여 핵심 기능 완성에 집중합니다.

- 일기당 이미지 최대 5장 업로드 (단일 API로 선 업로드 후 URL 반환)
- 업로드 전 이미지 미리보기
- 일기 상세 화면에서 이미지 목록 조회
- 첫 번째 업로드 이미지를 대표 이미지(썸네일)로 자동 사용

## 추후 확장 예정 기능

- 이미지 개별 삭제, 대표 이미지 직접 선택, 이미지 순서 변경
- 일기 수정 시 기존 이미지 유지 / 삭제 / 추가 처리 정교화
- 모바일 앱 확장
- 채팅 및 실시간 알림
- 공동구매 및 지도 API 연동
- 동영상 업로드
- AI 기반 일기 분석 및 성향 맞춤 메이트 추천 기능

## 기술 스택

### Backend
- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT (JSON Web Token)
- MySQL (또는 PostgreSQL)
- AWS S3 (이미지 스토리지)

### Frontend
- React
- Vite
- React Router
- Axios

### Tools
- IntelliJ IDEA
- Git / GitHub
- AI Assistant (보조 도구 및 더미 데이터 생성 활용)

## 주요 도메인

- User (회원)
- Dog (반려견)
- Diary (일기 및 피드)
- DiaryImage (일기 첨부 이미지)
- Comment (댓글)

## ERD 구조

- User `1:N` Dog
- User `1:N` Comment
- Dog `1:N` Diary
- Diary `1:N` DiaryImage
- Diary `1:N` Comment

## 핵심 API 구조

### Auth
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/logout` | 로그아웃 |

### Dog
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/dogs` | 반려견 등록 |
| GET | `/api/dogs` | 내 반려견 목록 조회 |
| PUT | `/api/dogs/{dogId}` | 반려견 정보 수정 |
| DELETE | `/api/dogs/{dogId}` | 반려견 삭제 |

### Diary & Feed
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/diaries` | 일기 작성 |
| GET | `/api/dogs/{dogId}/diaries` | 특정 반려견의 일기장 조회 |
| GET | `/api/diaries/feed` | 동네 피드 목록 조회 (지역 필터링) |
| GET | `/api/diaries/{diaryId}` | 일기 상세 조회 |
| PUT | `/api/diaries/{diaryId}` | 일기 수정 |
| DELETE | `/api/diaries/{diaryId}` | 일기 삭제 |

### Comment
| Method | URL | 설명 |
|---|---|---|
| GET | `/api/diaries/{diaryId}/comments`| 일기 댓글 목록 조회 |
| POST | `/api/diaries/{diaryId}/comments`| 댓글 작성 |
| DELETE | `/api/comments/{commentId}` | 댓글 삭제 |

### Image
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/images` | 이미지 파일 업로드 (URL 반환) |

## 1차 개발 목표

1차 개발에서는 부가적인 기능을 확장하기보다, 서비스의 뼈대가 되는 아래 흐름이 정상적으로 동작하는 것을 최우선 목표로 합니다.

1. 회원가입 및 로그인하여 인증 토큰 발급
2. 내 반려견 프로필 등록
3. 이미지 첨부와 함께 반려견 일기 작성
4. 우리 동네 피드에서 다른 사용자의 공개 일기 열람
5. 일기에 서로 댓글을 남기며 소통

## 프로젝트 진행 상태

- [x] 프로젝트 기획
- [x] 기능정의서 작성
- [x] ERD 설계
- [x] API 명세 작성
- [ ] Backend 개발
- [ ] Frontend 개발
- [ ] 화면 캡처 정리
- [ ] 트러블슈팅 정리