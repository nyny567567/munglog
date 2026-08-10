package com.munglog.service;

import com.munglog.dto.DiaryRequest;
import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.entity.DiaryPage;
import com.munglog.entity.Member;
import com.munglog.repository.DiaryPageRepository;
import com.munglog.repository.DiaryRepository;
import com.munglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryPageRepository diaryPageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createDiary(DiaryRequest request, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        Diary diary = Diary.createDiary(member);

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
                    return new DiaryResponse(
                            diary.getId(),
                            diary.getMember().getId(),
                            diary.getMember().getNickname(),
                            diary.getCreatedAt(),
                            pageResponses
                    );
                }).toList();
    }

    public DiaryResponse getDiary(Long id) {
       Diary diary =  diaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

       List<DiaryResponse.DiaryPageResponse> pageResponses = diary.getPages().stream()
               .sorted(Comparator.comparingInt(DiaryPage::getPageOrder))
               .map(p -> new DiaryResponse.DiaryPageResponse(p.getId(), p.getMediaUrl(), p.getContent(), p.getPageOrder())).toList();

       return new DiaryResponse(
               diary.getId(),
               diary.getMember().getId(),
               diary.getMember().getNickname(),
               diary.getCreatedAt(),
               pageResponses
       );
    }

    @Transactional
    public void deleteDiary(Long id, String loginEmail) {
        Diary diary = diaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

        validateDiaryOwner(diary, loginEmail);

        diaryRepository.delete(diary);
    }

    @Transactional
    public void updateDiaryEntirely(Long diaryId, DiaryRequest request, String loginEmail) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다."));

        validateDiaryOwner(diary, loginEmail);

        diary.getPages().clear();

        List<DiaryRequest.DiaryPageRequest> newPages = request.pages();
        for (int i = 0; i < newPages.size(); i++) {
            DiaryRequest.DiaryPageRequest pageReq = newPages.get(i);

            DiaryPage newPage = pageReq.toEntity(i + 1);

            diary.addPage(newPage);
        }
    }

    private void validateDiaryOwner(Diary diary, String loginEmail) {
        if (!diary.getMember().getEmail().equals(loginEmail)) {
            throw new IllegalArgumentException("본인의 일기만 접근할 수 있습니다.");
        }
    }

}
