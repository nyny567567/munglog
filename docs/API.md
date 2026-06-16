# API 명세서

> **공통 안내사항**
> - 회원가입, 로그인, 피드 조회 등 일부 Public API를 제외한 모든 API는 Header에 인증 토큰이 필요합니다.
> - `Authorization: Bearer {Access_Token}`

## 1. Auth (인증)

### 회원가입
- **URL**: `POST /api/auth/signup`
- **설명**: 신규 사용자를 등록합니다.
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string",
    "nickname": "string",
    "regionCode": "string",
    "regionName": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "userId": 1 }
  }
  ```

### 로그인
- **URL**: `POST /api/auth/login`
- **설명**: 이메일과 비밀번호로 로그인합니다.
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": {
      "accessToken": "string"
    }
  }
  ```

### 로그아웃
- **URL**: `POST /api/auth/logout`
- **설명**: 현재 로그인된 사용자의 세션을 종료(또는 토큰 만료)합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

---

## 2. Dog (반려견 프로필)

### 반려견 등록
- **URL**: `POST /api/dogs`
- **설명**: 새로운 반려견 정보를 등록합니다.
- **Request Body**:
  ```json
  {
    "name": "string",
    "breed": "string",
    "birthDate": "yyyy-mm-dd",
    "familyDate": "yyyy-mm-dd",
    "gender": "string",
    "neutered": "boolean",
    "description": "string",
    "profileImageUrl": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "dogId": 1 }
  }
  ```

### 반려견 목록 조회
- **URL**: `GET /api/dogs`
- **설명**: 로그인한 사용자가 등록한 반려견 목록을 조회합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "dogId": 1,
        "name": "string",
        "profileImageUrl": "string"
      }
    ]
  }
  ```

### 반려견 수정
- **URL**: `PUT /api/dogs/{dogId}`
- **설명**: 등록된 반려견 정보를 수정합니다.
- **Request Body**:
  ```json
  {
    "name": "string",
    "breed": "string",
    "birthDate": "yyyy-mm-dd",
    "familyDate": "yyyy-mm-dd",
    "gender": "string",
    "neutered": "boolean",
    "description": "string",
    "profileImageUrl": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

### 반려견 삭제
- **URL**: `DELETE /api/dogs/{dogId}`
- **설명**: 등록된 반려견 정보를 삭제합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

---

## 3. Diary (일기 및 피드)

### 일기 작성
- **URL**: `POST /api/diaries`
- **설명**: 일기를 작성하며, 공개 범위와 댓글 허용 여부를 설정합니다.
- **Request Body**:
  ```json
  {
    "dogId": 1,
    "title": "string",
    "content": "string",
    "diaryDate": "yyyy-mm-dd",
    "imageUrls": ["string", "string"],
    "isPublic": true,
    "isCommentAllowed": true 
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "diaryId": 1 }
  }
  ```

### 특정 반려견의 일기장 조회 (마이페이지용)
- **URL**: `GET /api/dogs/{dogId}/diaries`
- **설명**: 특정 강아지의 일기 목록을 최신순으로 조회합니다. (비공개 일기 포함 여부는 권한에 따라 처리)
- **Request Parameters**:
  - `page` (int, 선택): 페이지 번호 (기본값: 0)
  - `size` (int, 선택): 페이지당 데이터 개수 (기본값: 10)
- **Response**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "diaryId": 1,
        "title": "string",
        "diaryDate": "yyyy-mm-dd",
        "thumbnailUrl": "string",
        "isPublic": true
      }
    ]
  }
  ```

### 일기 조회 (상세)
- **URL**: `GET /api/diaries/{diaryId}`
- **설명**: 특정 일기의 상세 내용을 조회합니다. 조회 시 해당 일기의 조회수(`viewCount`)가 증가합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success",
    "data": {
      "diaryId": 1,
      "dogId": 1,
      "title": "string",
      "content": "string",
      "diaryDate": "yyyy-mm-dd",
      "imageUrls": ["string", "string"],
      "isPublic": true,
      "isCommentAllowed": true,
      "viewCount": 1
    }
  }
  ```

### 동네 피드 목록 조회
- **URL**: `GET /api/diaries/feed`
- **설명**: 지정된 지역 코드(`regionCode`)를 기준으로 전체 공개된 일기 목록을 최신순(작성일 역순)으로 조회합니다.
- **Request Parameters**:
  - `regionCode` (string, 필수): 필터링할 행정동 코드
  - `page` (int, 선택): 페이지 번호 (기본값: 0)
  - `size` (int, 선택): 페이지당 데이터 개수 (기본값: 10)
- **Response**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "diaryId": 1,
        "nickname": "string",
        "title": "string",
        "thumbnailUrl": "string",
        "viewCount": 0,
        "commentCount": 0,
        "createdAt": "yyyy-mm-dd HH:mm:ss"
      }
    ]
  }
  ```

### 일기 수정
- **URL**: `PUT /api/diaries/{diaryId}`
- **설명**: 작성자가 일기 내용, 공개 범위, 댓글 허용 여부를 수정합니다.
- **Request Body**:
  ```json
  {
    "title": "string",
    "content": "string",
    "imageUrls": ["string", "string"],
    "isPublic": true,
    "isCommentAllowed": true 
  }
  ```
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

### 일기 삭제
- **URL**: `DELETE /api/diaries/{diaryId}`
- **설명**: 작성자가 일기를 삭제합니다. (Soft Delete 처리)
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

---

## 4. Comment (댓글)

### 댓글 목록 조회
- **URL**: `GET /api/diaries/{diaryId}/comments`
- **설명**: 특정 일기에 달린 모든 댓글 목록을 작성일 기준 오름차순(과거순)으로 조회합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success",
    "data": [
      {
        "commentId": 1,
        "userId": 1,
        "nickname": "string",
        "content": "string",
        "createdAt": "yyyy-mm-dd HH:mm:ss"
      }
    ]
  }
  ```

### 댓글 작성
- **URL**: `POST /api/diaries/{diaryId}/comments`
- **설명**: 특정 일기에 댓글을 작성합니다. (일기 주인이 댓글 허용으로 설정한 경우만 가능)
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "commentId": 1 }
  }
  ```

### 댓글 삭제
- **URL**: `DELETE /api/comments/{commentId}`
- **설명**: 댓글 작성자 또는 해당 일기의 주인(작성자)이 댓글을 삭제합니다.
- **Request Body**: 없음
- **Response**:
  ```json
  {
    "status": "success"
  }
  ```

---

## 5. Image (이미지 업로드)

### 이미지 파일 업로드
- **URL**: `POST /api/images`
- **설명**: 일기 작성 시 첨부할 이미지 파일을 서버(또는 외부 스토리지)에 업로드하고, 저장된 이미지의 URL을 반환받습니다.
- **Content-Type**: `multipart/form-data`
- **Request Parameters**:
  - `file` (File): 업로드할 이미지 파일 (최대 5장 처리 가능)
- **Response**:
  ```json
  {
    "status": "success",
    "data": { 
      "imageUrl": "https://munglog.com/images/uuid-image.jpg"
    }
  }
  ```