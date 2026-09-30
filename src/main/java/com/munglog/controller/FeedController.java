package com.munglog.controller;

import com.munglog.dto.DiaryResponse;
import com.munglog.service.DiaryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final DiaryService diaryService;

    // 지역 피드 조회 API
    @GetMapping
    public ResponseEntity<Page<DiaryResponse>> getDiariesByRegionCode(
            @RequestParam @NotBlank String regionCode,
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {
        Page<DiaryResponse> responses =
                diaryService.getDiariesByRegionCode(regionCode, page, size);
        return ResponseEntity.ok(responses);
    }


}
