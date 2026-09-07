package com.munglog.dto;

import com.munglog.entity.DiaryPage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DiaryRequest(
        LocalDate date,
        LocalTime time,
        String weather,
        boolean isPublic,
        boolean isCommentAllowed,
        List<DiaryPageRequest> pages
) {
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
