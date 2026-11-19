package com.munglog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class S3PresignerTest {

    @Autowired
    private S3Presigner s3Presigner;

    @Test
    void succeedsWhenGeneratingPresignedUploadUrl() {

        // Given
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("munglog-media-2026")
                .key("test/presigned-upload.txt")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        // When
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // Then
        assertThat(presignedRequest.url()).isNotNull();
    }
}
