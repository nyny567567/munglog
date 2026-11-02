package com.munglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Media {

    private static final long MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String s3Key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private long fileSize;

    @Column
    private Integer durationSeconds;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Media(
            Member owner,
            String s3Key,
            MediaType mediaType,
            long fileSize,
            Integer durationSeconds) {

        validate(owner, s3Key, mediaType, fileSize, durationSeconds);

        this.owner = owner;
        this.s3Key = s3Key;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.durationSeconds = durationSeconds;
    }

    private static void validate(
            Member owner,
            String s3Key,
            MediaType mediaType,
            long fileSize,
            Integer durationSeconds) {

        if (owner == null) {
            throw new IllegalArgumentException("업로드 회원은 필수입니다.");
        }
        if (s3Key == null || s3Key.isBlank()) {
            throw new IllegalArgumentException("S3 객체 키는 비어 있을 수 없습니다.");
        }
        if (fileSize <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다.");
        }
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("파일 크기는 20MiB를 초과할 수 없습니다.");
        }
        if (mediaType == MediaType.VIDEO &&
                (durationSeconds == null || durationSeconds <= 0)) {
            throw new IllegalArgumentException("동영상 재생 시간은 0초를 넘어야 합니다.");
        }
    }

    public static Media createImage(Member owner, String s3Key, long fileSize) {
        return new Media(owner, s3Key, MediaType.IMAGE, fileSize, null);
    }

    public static Media createVideo(Member owner, String s3Key, long fileSize, Integer durationSeconds) {
        return new Media(owner, s3Key, MediaType.VIDEO, fileSize, durationSeconds);
    }
}
