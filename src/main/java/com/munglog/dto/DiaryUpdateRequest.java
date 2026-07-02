package com.munglog.dto;

import java.util.List;

public record DiaryUpdateRequest(List<DiaryPageUpdateRequest> pages) {
    public record DiaryPageUpdateRequest(Long pageId, String mediaUrl, String content, int pageOrder) {}
}
