package com.munglog.validator;

import com.munglog.entity.MediaType;

import java.util.Set;

public class MediaFileValidator {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/heic"
    );

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/quicktime"
    );

    private static final long MAX_IMAGE_SIZE = 20L * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 300L * 1024 * 1024;

    public MediaType validate(String contentType, long fileSize) {

        if (contentType == null) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            validateFileSize(fileSize, MAX_IMAGE_SIZE, "이미지 파일 크기는 20MiB를 초과할 수 없습니다.");
            return MediaType.IMAGE;
        }

        if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            validateFileSize(fileSize, MAX_VIDEO_SIZE, "동영상 파일 크기는 300MiB를 초과할 수 없습니다.");
            return MediaType.VIDEO;
        }

        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
    }

    private void validateFileSize(long fileSize, long maxSize, String maxSizeErrorMessage) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다.");
        }
        if (fileSize > maxSize) {
            throw new IllegalArgumentException(maxSizeErrorMessage);
        }
    }
}
