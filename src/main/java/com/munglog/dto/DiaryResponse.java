package com.munglog.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DiaryResponse(Long diaryId, LocalDateTime createdAt, List<DiaryPageResponse> pages) {
    public record DiaryPageResponse(Long pageId, String mediaUrl, String content, int pageOrder) {}
}
