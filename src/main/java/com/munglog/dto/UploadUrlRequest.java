package com.munglog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UploadUrlRequest(
        @NotBlank
        String fileName,

        @NotNull
        @Positive
        Long fileSize,

        @NotBlank
        String contentType
) {
}
