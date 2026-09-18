package com.munglog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DiaryResponse(
        Long diaryId,
        Long authorId,
        String authorNickname,
        LocalDate diaryDate,
        LocalTime diaryTime,
        String weather,
        boolean isPublic,
        boolean isCommentAllowed,
        LocalDateTime createdAt,
        List<DiaryPageResponse> pages) {
    public record DiaryPageResponse(Long pageId, String mediaUrl, String content, int pageOrder) {}
}
