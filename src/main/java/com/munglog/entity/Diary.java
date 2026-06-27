package com.munglog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryPage> pages = new ArrayList<>();

    //페이지와 일기 부모 - 자식 관계 연결
    public void addPage(DiaryPage page) {
        this.pages.add(page);
        page.assignDiary(this);
    }

    private LocalDateTime createdAt;

    public Diary(String content, LocalDateTime createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
