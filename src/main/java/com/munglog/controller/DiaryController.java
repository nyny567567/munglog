package com.munglog.controller;

import com.munglog.entity.Diary;
import com.munglog.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<Diary> createDiary(@RequestBody DiaryRequest request) {
        Diary savedDiary = diaryService.createDiary(request.content());
        return ResponseEntity.ok(savedDiary);
    }

    public record DiaryRequest(String content) {}
}
