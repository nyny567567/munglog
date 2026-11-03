package com.munglog.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MediaTest {

    @Test
    @DisplayName("이미지를 생성하면 전달한 정보와 IMAGE 타입이 올바르게 설정된다")
    void createImage_succeeds() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When
        Media testImage = Media.createImage(member, "s3KeyForTest", 20L * 1024 * 1024);

        // Then
        assertThat(testImage.getOwner()).isSameAs(member);
        assertThat(testImage.getMediaType()).isEqualTo(MediaType.IMAGE);
        assertThat(testImage.getS3Key()).isEqualTo("s3KeyForTest");
        assertThat(testImage.getFileSize()).isEqualTo(20L * 1024 * 1024);
        assertThat(testImage.getDurationSeconds()).isNull();
    }

    @Test
    @DisplayName("파일 크기가 0이면 이미지 생성에 실패한다")
    void createImage_failsWhenFileSizeIsZero() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토마미");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, "s3KeyForTest", 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 크기는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("파일 크기가 0보다 작으면 이미지 생성에 실패한다")
    void createImage_failsWhenFileSizeIsNegative() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토마미");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, "s3KeyForTest", -50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 크기는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("이미지 파일 크기가 20MiB를 초과하면 이미지 생성에 실패한다")
    void createImage_failsWhenFileSizeExceedsMax() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토마미");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, "s3KeyForTest", 20L * 1024 * 1024 + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 파일 크기는 20MiB를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("업로드 회원이 null이면 이미지 생성에 실패한다")
    void createImage_failsWhenOwnerIsNull() {

        // When & Then
        assertThatThrownBy(() -> Media.createImage(null, "s3KeyForTest", 50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("업로드 회원은 필수입니다.");
    }

    @Test
    @DisplayName("S3 객체 키가 null이면 이미지 생성에 실패한다")
    void createImage_failsWhenS3KeyIsNull() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토야");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, null, 50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("S3 객체 키는 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("S3 객체 키가 빈 문자열이면 이미지 생성에 실패한다")
    void createImage_failsWhenS3KeyIsEmpty() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토주인");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, "", 50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("S3 객체 키는 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("S3 객체 키가 공백으로만 이루어져 있으면 이미지 생성에 실패한다")
    void createImage_failsWhenS3KeyIsBlank() {

        // Given
        Member testMember = createMember("testMember@munglog.com", "토토주인");

        // When & Then
        assertThatThrownBy(() -> Media.createImage(testMember, "      ", 50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("S3 객체 키는 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("동영상을 생성하면 전달한 정보와 VIDEO 타입이 올바르게 설정된다")
    void createVideo_succeeds() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When
        Media testVideo = Media.createVideo(member, "s3KeyForTest", 50L, 1);

        // Then
        assertThat(testVideo.getOwner()).isSameAs(member);
        assertThat(testVideo.getMediaType()).isEqualTo(MediaType.VIDEO);
        assertThat(testVideo.getS3Key()).isEqualTo("s3KeyForTest");
        assertThat(testVideo.getFileSize()).isEqualTo(50L);
        assertThat(testVideo.getDurationSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("동영상 크기가 정확히 300MiB이면 생성에 성공한다")
    void createVideo_succeedsWhenFileSizeIsMax() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When
        Media testVideo = Media.createVideo(member, "s3KeyForTest", 300L * 1024 * 1024, 1);

        // Then
        assertThat(testVideo.getFileSize()).isEqualTo(300L * 1024 * 1024);
    }

    @Test
    @DisplayName("동영상 크기가 300MiB를 초과하면 생성에 실패한다")
    void createVideo_failsWhenFileSizeExceedsMax() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When & Then
        assertThatThrownBy(() -> Media.createVideo(member, "s3KeyForTest", 300L * 1024 * 1024 + 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 파일 크기는 300MiB를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("동영상 재생시간이 정확히 30초면 생성에 성공한다")
    void createVideo_succeedsWhenDurationIsMax() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When
        Media testVideo = Media.createVideo(member, "s3KeyForTest", 50L, 30);

        // Then
        assertThat(testVideo.getDurationSeconds()).isEqualTo(30);
    }

    @Test
    @DisplayName("동영상 재생시간이 30초를 초과하면 생성에 실패한다")
    void createVideo_failsWhenDurationExceedsMax() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When & Then
        assertThatThrownBy(() -> Media.createVideo(member, "s3KeyForTest", 50L, 31))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 재생 시간은 0초 초과 30초 이하여야 합니다.");
    }

    @Test
    @DisplayName("동영상 재생시간이 null이면 동영상 생성에 실패한다")
    void createVideo_failsWhenDurationIsNull() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When & Then
        assertThatThrownBy(() -> Media.createVideo(member, "s3KeyForTest", 50L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 재생 시간은 0초 초과 30초 이하여야 합니다.");
    }

    @Test
    @DisplayName("동영상 재생시간이 0이면 동영상 생성에 실패한다")
    void createVideo_failsWhenDurationIsZero() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When & Then
        assertThatThrownBy(() -> Media.createVideo(member, "s3KeyForTest", 50L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 재생 시간은 0초 초과 30초 이하여야 합니다.");
    }

    @Test
    @DisplayName("동영상 재생시간이 음수이면 동영상 생성에 실패한다")
    void createVideo_failsWhenDurationIsNegative() {

        // Given
        Member member = createMember("testMember@munglog.com", "토토누나");

        // When & Then
        assertThatThrownBy(() -> Media.createVideo(member, "s3KeyForTest", 50L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 재생 시간은 0초 초과 30초 이하여야 합니다.");
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .password("1234")
                .nickname(nickname)
                .regionCode("SAMSONG")
                .build();
    }

}