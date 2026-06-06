# ERD

## 엔티티

- User
- Dog
- Diary
- DiaryImage
- Post
- Comment

## 관계

- User 1:N Dog
- Dog 1:N Diary
- Diary 1:N DiaryImage
- User 1:N Post
- Post 1:N Comment
- User 1:N Comment


## User

| 컬럼명         | 타입           | 설명         |
| ----------- | ------------ | ---------- |
| id          | BIGINT       | 회원 ID (PK) |
| email       | VARCHAR(255) | 이메일        |
| password    | VARCHAR(255) | 비밀번호       |
| nickname    | VARCHAR(50)  | 닉네임        |
| region_code | VARCHAR(20)  | 행정동 코드     |
| region_name | VARCHAR(100) | 지역명        |
| created_at  | DATETIME     | 생성일        |
| updated_at  | DATETIME     | 수정일        |


## Dog

| 컬럼명               | 타입           | 설명          |
| ----------------- | ------------ | ----------- |
| id                | BIGINT       | 반려견 ID (PK) |
| user_id           | BIGINT       | 회원 ID (FK)  |
| name              | VARCHAR(50)  | 이름          |
| breed             | VARCHAR(50)  | 견종          |
| birth_date        | DATE         | 생일 (선택)     |
| family_date       | DATE         | 가족이 된 날     |
| gender            | VARCHAR(10)  | 성별          |
| neutered          | BOOLEAN      | 중성화 여부      |
| description       | TEXT         | 소개글         |
| profile_image_url | VARCHAR(500) | 프로필 이미지     |
| created_at        | DATETIME     | 생성일         |
| updated_at        | DATETIME     | 수정일         |

