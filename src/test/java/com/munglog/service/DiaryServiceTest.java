package com.munglog.service;

import com.munglog.entity.Diary;
import com.munglog.repository.DiaryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;

    @Autowired
    DiaryRepository diaryRepository;

    @Test
    void 일기_정상_저장_성공_테스트() {
        String content = "일기 저장 테스트 content";

        Diary savedDiary = diaryService.createDiary(content);

        assertThat(savedDiary.getId()).isNotNull();
        assertThat(savedDiary.getContent()).isEqualTo("일기 저장 테스트 content");
    }

    @Test
    void 빈_일기_저장시_에러발생_테스트() {
        String emptyContent = "  ";

        assertThatThrownBy(() -> diaryService.createDiary(emptyContent)).isInstanceOf(IllegalArgumentException.class).hasMessage("일기 내용은 비어있을 수 없습니다!");
    }

}