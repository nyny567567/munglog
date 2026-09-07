package com.munglog.service;

import com.munglog.dto.DiaryRequest;
import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.entity.Member;
import com.munglog.repository.DiaryRepository;

import com.munglog.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("사용자 입력(날짜, 시간, 날씨)을 포함한 일기가 정상적으로 생성")
    void createDiary_success_test() {
        // Given
        Member testMember = Member.builder()
                .email("test@munglong.com")
                .password("1234")
                .nickname("테스터1")
                .build();
        memberRepository.save(testMember);

        DiaryRequest.DiaryPageRequest pageRequest = new DiaryRequest.DiaryPageRequest(
                "http://example.com/image.jpg",
                "테스트 일기 본문",
                1
        );

        DiaryRequest request = new DiaryRequest(
                LocalDate.of(2026, 9, 7),
                LocalTime.of(18, 21),
                "sunny",
                true,
                true,
                List.of(pageRequest)
        );

        // When
        Long savedDiaryId = diaryService.createDiary(request, "test@munglong.com");

        // Then
        Diary savedDiary = diaryRepository.findById(savedDiaryId).orElseThrow();

        assertThat(savedDiary.getDiaryDate()).isEqualTo(LocalDate.of(2026, 9, 7));
        assertThat(savedDiary.getDiaryTime()).isEqualTo(LocalTime.of(18, 21));
        assertThat(savedDiary.getWeather()).isEqualTo("sunny");
        assertThat(savedDiary.isPublic()).isTrue();
        assertThat(savedDiary.isCommentAllowed()).isTrue();
        assertThat(savedDiary.getPages().get(0).getMediaUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(savedDiary.getPages().get(0).getContent()).isEqualTo("테스트 일기 본문");


    }
}