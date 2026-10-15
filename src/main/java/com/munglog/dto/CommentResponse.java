package com.munglog.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long authorId,
        String nickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
