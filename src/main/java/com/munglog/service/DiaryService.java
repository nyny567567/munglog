package com.munglog.service;

import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public Diary createDiary(String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("일기 내용은 비어있을 수 없습니다!");
        }

        LocalDateTime now = LocalDateTime.now();

        Diary newDiary = new Diary(content, now);

        return diaryRepository.save(newDiary);
    }

    public List<DiaryResponse> getAllDiaries() {
        return diaryRepository.findAll().stream().map(diary -> new DiaryResponse(diary.getId(), diary.getContent())).toList();
    }

}
