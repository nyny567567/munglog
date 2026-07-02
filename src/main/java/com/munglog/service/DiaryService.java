package com.munglog.service;

import com.munglog.dto.DiaryRequest;
import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.entity.DiaryPage;
import com.munglog.repository.DiaryPageRepository;
import com.munglog.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryPageRepository diaryPageRepository;

    @Transactional
    public Long createDiary(DiaryRequest request) {

        Diary diary = Diary.createDiary();

        request.pages().forEach(pageDto -> {
            DiaryPage page = DiaryPage.builder().mediaUrl(pageDto.mediaUrl())
                    .content(pageDto.content())
                    .pageOrder(pageDto.pageOrder()).build();
            diary.addPage(page);
        });
        return diaryRepository.save(diary).getId();
    }

    public List<DiaryResponse> getAllDiaries() {
        return diaryRepository.findAll().stream()
                .map(diary -> {
                    List<DiaryResponse.DiaryPageResponse> pageResponses = diary.getPages().stream()
                            .map(p -> new DiaryResponse.DiaryPageResponse(p.getId(), p.getMediaUrl(), p.getContent(), p.getPageOrder()))
                            .toList();
                    return new DiaryResponse(diary.getId(), diary.getCreatedAt(), pageResponses);
                }).toList();
    }

    public DiaryResponse getDiary(Long id) {
       Diary diary =  diaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

       List<DiaryResponse.DiaryPageResponse> pageResponses = diary.getPages().stream()
               .map(p -> new DiaryResponse.DiaryPageResponse(p.getId(), p.getMediaUrl(), p.getContent(), p.getPageOrder())).toList();

       return new DiaryResponse((diary.getId()), diary.getCreatedAt(), pageResponses);
    }

    @Transactional
    public void updatePage(Long diaryId, Long pageId, DiaryRequest.DiaryPageRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

        DiaryPage page = diaryPageRepository.findById(pageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 페이지를 찾을 수 없습니다."));

        page.updatePage(request.mediaUrl(), request.content(), request.pageOrder());

        List<DiaryPage> pages = diary.getPages();
        pages.sort(Comparator.comparingInt(DiaryPage::getPageOrder));

        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).updateOrder(i + 1);
        }
    }

    @Transactional
    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

        diaryRepository.delete(diary);
    }
}
