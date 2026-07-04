package com.munglog.dto;

import com.munglog.entity.DiaryPage;

import java.util.List;

public record DiaryRequest(List<DiaryPageRequest> pages) {
    public record DiaryPageRequest (String mediaUrl, String content, int pageOrder) {
        public DiaryPage toEntity(int newOrder) {
            return DiaryPage.builder()
                    .mediaUrl(this.mediaUrl)
                    .content(this.content)
                    .pageOrder(newOrder)
                    .build();
        }
    }
}
