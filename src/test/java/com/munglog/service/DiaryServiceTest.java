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

    @Test
    void 일기_단건_조회_성공_테스트() {
        Diary savedDiary = diaryService.createDiary("단건 조회 테스트용 일기");
        Long targetId = savedDiary.getId();

        DiaryResponse response = diaryService.getDiary(targetId);

        assertThat(response.id()).isEqualTo(targetId);
        assertThat(response.content()).isEqualTo("단건 조회 테스트용 일기");
    }

    @Test
    void 존재하지_않는_일기_조회시_에러발생_테스트() {
        Long invalidId = 999L;

        assertThatThrownBy(() -> diaryService.getDiary(invalidId)).isInstanceOf(IllegalArgumentException.class).hasMessage("해당 일기를 찾을 수 없습니다.");
    }

    @Test
    void 일기_수정_성공_테스트() {
        Diary savedDiary = diaryService.createDiary("수정 전 일기 내용");
        Long targetId = savedDiary.getId();

        diaryService.updateDiary(targetId, "수정 후 일기 내용");

        DiaryResponse response = diaryService.getDiary(targetId);
        assertThat(response.content()).isEqualTo("수정 후 일기 내용");
    }

    @Test
    void 존재하지_않는_다이어리_수정시_에러발생_테스트() {
        Long invalidId = 999L;

        assertThatThrownBy(() -> diaryService.updateDiary(invalidId, "새로운 내용")).isInstanceOf(IllegalArgumentException.class).hasMessage("해당 일기를 찾을 수 없습니다.");
    }

    @Test
    void 일기_삭제_성공_테스트() {
        Diary savedDiary = diaryService.createDiary("삭제될 일기 내용");
        Long targetId = savedDiary.getId();

        diaryService.deleteDiary(targetId);

        assertThatThrownBy(() -> diaryService.getDiary(targetId)).isInstanceOf(IllegalArgumentException.class).hasMessage("해당 일기를 찾을 수 없습니다.");
    }

    @Test
    void 존재하지_않는_일기_삭제시_에러발생_테스트() {
        Long invalidId = 999L;

        assertThatThrownBy(() -> diaryService.deleteDiary(invalidId)).isInstanceOf(IllegalArgumentException.class).hasMessage("해당 일기를 찾을 수 없습니다.");
    }

}