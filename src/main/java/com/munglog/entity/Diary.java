package com.munglog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) //시간 자동 기록
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryPage> pages = new ArrayList<>();

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean isCommentAllowed;

    @Column(nullable = false)
    private LocalDate diaryDate;

    @Column(nullable = false)
    private LocalTime diaryTime;

    @Column(length = 50)
    private String weather;

    public static Diary createDiary(
            Member member,
            LocalDate diaryDate,
            LocalTime diaryTime,
            String weather,
            boolean isPublic,
            boolean isCommentAllowed) {
        Diary diary = new Diary();
        diary.member = member;
        diary.diaryDate = diaryDate;
        diary.diaryTime = diaryTime;
        diary.weather = weather;
        diary.isPublic = isPublic;
        diary.isCommentAllowed = isCommentAllowed;
        return diary;
    }

    //페이지와 일기 부모 - 자식 관계 연결
    public void addPage(DiaryPage page) {
        this.pages.add(page);
        page.assignDiary(this);
    }

    public void update(boolean isPublic, boolean isCommentAllowed) {
        this.isPublic = isPublic;
        this.isCommentAllowed = isCommentAllowed;
    }
}
