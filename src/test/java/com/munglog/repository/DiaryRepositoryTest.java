package com.munglog.repository;

import com.munglog.entity.Diary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiaryRepositoryTest {

    @Autowired
    private DiaryRepository diaryRepository;

    @Test
    void 일기저장_및_조회_테스트() {
        Diary newDiary = new Diary("백엔드 데이터베이스 연동 테스트", LocalDateTime.now());

        diaryRepository.save(newDiary);

        List<Diary> diaryList = diaryRepository.findAll();
        Diary savedDiary = diaryList.get(0);

        assertThat(savedDiary.getContent()).isEqualTo("백엔드 데이터베이스 연동 테스트");
        System.out.println("======== 저장된 일기 번호: " + savedDiary.getId() + "========");
    }

}