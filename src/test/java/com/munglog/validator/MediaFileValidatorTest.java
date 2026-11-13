package com.munglog.validator;

import com.munglog.entity.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MediaFileValidatorTest {

    private final MediaFileValidator validator = new MediaFileValidator();

    @Test
    @DisplayName("1MiB JPEG 이미지는 검증에 성공한다")
    void validate_succeedsWhenValidJpegImage() {

        // Given
        long fileSize = 1024L * 1024;

        // When
        MediaType result = validator.validate("image/jpeg", fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.IMAGE);
    }

    @Test
    @DisplayName("허용되지 않은 MIME 타입이면 예외가 발생한다")
    void validate_failsWhenUnsupportedContentType() {

        // Given
        String contentType = "application/pdf";
        long fileSize = 1024L * 1024;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 파일 형식입니다.");
    }

    @Test
    @DisplayName("1MiB PNG 이미지는 검증에 성공한다")
    void validate_succeedsWhenValidPngImage() {

        // Given
        String contentType = "image/png";
        long fileSize = 1024L * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.IMAGE);
    }

    @Test
    @DisplayName("1MiB WebP 이미지는 검증에 성공한다")
    void validate_succeedsWhenValidWebPImage() {

        // Given
        String contentType = "image/webp";
        long fileSize = 1024L * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.IMAGE);
    }

    @Test
    @DisplayName("1MiB HEIC 이미지는 검증에 성공한다")
    void validate_succeedsWhenValidHeicImage() {

        // Given
        String contentType = "image/heic";
        long fileSize = 1024L * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.IMAGE);
    }

    @Test
    @DisplayName("10MiB MP4 동영상은 검증에 성공한다")
    void validate_succeedsWhenValidMp4Video() {

        // Given
        String contentType = "video/mp4";
        long fileSize = 10L * 1024 * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.VIDEO);
    }

    @Test
    @DisplayName("10MiB QuickTime 동영상은 검증에 성공한다")
    void validate_succeedsWhenValidQuickTimeVideo() {

        // Given
        String contentType = "video/quicktime";
        long fileSize = 10L * 1024 * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.VIDEO);
    }

    @Test
    @DisplayName("21MiB JPEG 이미지는 크기 제한 초과로 예외가 발생한다")
    void validate_failsWhenImageExceedsSizeLimit() {

        // Given
        String contentType = "image/jpeg";
        long fileSize = 21L * 1024 * 1024;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 파일 크기는 20MiB를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("301MiB MP4 동영상은 크기 제한 초과로 예외가 발생한다")
    void validate_failsWhenVideoExceedsSizeLimit() {

        // Given
        String contentType = "video/mp4";
        long fileSize = 301L * 1024 * 1024;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동영상 파일 크기는 300MiB를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("20MiB JPEG 이미지는 검증에 성공한다")
    void validate_succeedsWhenImageIsExactlyMaxSize() {

        // Given
        String contentType = "image/jpeg";
        long fileSize = 20L * 1024 * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.IMAGE);
    }

    @Test
    @DisplayName("300MiB MP4 동영상은 검증에 성공한다")
    void validate_succeedsWhenVideoIsExactlyMaxSize() {

        // Given
        String contentType = "video/mp4";
        long fileSize = 300L * 1024 * 1024;

        // When
        MediaType result = validator.validate(contentType, fileSize);

        // Then
        assertThat(result).isEqualTo(MediaType.VIDEO);
    }

    @Test
    @DisplayName("0바이트 JPEG 이미지는 검증에 실패한다")
    void validate_failsWhenFileSizeIsZero() {

        // Given
        String contentType = "image/jpeg";
        long fileSize = 0;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 크기는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("-1바이트 JPEG 이미지는 검증에 실패한다")
    void validate_failsWhenFileSizeIsNegative() {

        // Given
        String contentType = "image/jpeg";
        long fileSize = -1L;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일 크기는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("MIME 타입이 null이면 검증에 실패한다")
    void validate_failsWhenContentTypeIsNull() {

        // Given
        String contentType = null;
        long fileSize = 1024L;

        // When & Then
        assertThatThrownBy(() -> validator.validate(contentType, fileSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 파일 형식입니다.");
    }
}
