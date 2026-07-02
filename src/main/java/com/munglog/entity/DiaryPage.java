package com.munglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //영상이나 이미지 주소
    @Column(length = 2048)
    private String mediaUrl;

    //일기 텍스트 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    //페이지 순서
    private int pageOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Builder
    public DiaryPage(String mediaUrl, String content, int pageOrder) {
        this.mediaUrl = mediaUrl;
        this.content = content;
        this.pageOrder = pageOrder;
    }

    //부모 일기장과 일기장 페이지 연결 (외부 노출 최소화)
    void assignDiary(Diary diary) {
        this.diary = diary;
    }

    public void updatePage(String mediaUrl, String content, int pageOrder) {
        this.mediaUrl = mediaUrl;
        this.content = content;
        this.pageOrder = pageOrder;
    }

    public void updateOrder(int pageOrder) {
        this.pageOrder = pageOrder;
    }
}
