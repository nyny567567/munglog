package com.munglog.controller;

import com.munglog.dto.DiaryRequest;
import com.munglog.dto.DiaryResponse;
import com.munglog.dto.DiaryUpdateRequest;
import com.munglog.entity.Diary;
import com.munglog.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    //일기 생성 API
    @PostMapping
    public ResponseEntity<Diary> createDiary(@RequestBody DiaryRequest request) {
        Diary savedDiary = diaryService.createDiary(request.content());
        return ResponseEntity.ok(savedDiary);
    }

    //전체 조회 API
    @GetMapping
    public ResponseEntity<List<DiaryResponse>> getAllDiaries() {
        List<DiaryResponse> responses = diaryService.getAllDiaries();
        return ResponseEntity.ok(responses);
    }

    //단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable Long id) {
        DiaryResponse response = diaryService.getDiary(id);
        return ResponseEntity.ok(response);
    }

    //일기 수정 API
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDiary(@PathVariable Long id, @RequestBody DiaryUpdateRequest request) {
        diaryService.updateDiary(id, request.content());
        return ResponseEntity.ok().build();
    }

    //일기 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return ResponseEntity.ok().build();
    }

}
