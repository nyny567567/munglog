package com.munglog.service;

import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.repository.DiaryRepository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
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

    @Test
    void 일기_전체_조회_성공_테스트() {
        diaryService.createDiary("첫 번째 테스트 일기");
        diaryService.createDiary("두 번째 테스트 일기");

        List<DiaryResponse> responses = diaryService.getAllDiaries();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).content()).isEqualTo("첫 번째 테스트 일기");
        assertThat(responses.get(1).content()).isEqualTo("두 번째 테스트 일기");

    }

}