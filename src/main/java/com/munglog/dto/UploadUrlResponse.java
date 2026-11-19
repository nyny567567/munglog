package com.munglog.dto;

public record UploadUrlResponse(
        String uploadUrl,
        String s3Key
) {
}
