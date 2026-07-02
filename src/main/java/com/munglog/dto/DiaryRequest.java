package com.munglog.dto;

import java.util.List;

public record DiaryRequest(List<DiaryPageRequest> pages) {
    public record DiaryPageRequest (String mediaUrl, String content, int pageOrder) {}
}
