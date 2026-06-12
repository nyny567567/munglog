# API 명세서

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

---

## 3. Diary (일기)

### 일기 작성
- **URL**: `POST /api/diaries`
- **설명**: 새로운 일기를 작성합니다.
- **Request Body**:
  ```json
  {
    "dogId": 1,
    "title": "string",
    "content": "string",
    "diaryDate": "yyyy-mm-dd",
    "imageUrls": ["string", "string"] 
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "diaryId": 1 }
  }
  ```

---

## 4. Post (지역 게시판)

### 게시글 작성
- **URL**: `POST /api/posts`
- **설명**: 새로운 지역 게시글을 작성합니다.
- **Request Body**:
  ```json
  {
    "title": "string",
    "content": "string",
    "regionCode": "string",
    "regionName": "string"
  }
  ```
- **Response**:
  ```json
  {
    "status": "success",
    "data": { "postId": 1 }
  }
  ```

---

## 5. Comment (댓글)

### 댓글 작성
- **URL**: `POST /api/posts/{postId}/comments`
- **설명**: 특정 게시글에 댓글을 작성합니다.
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