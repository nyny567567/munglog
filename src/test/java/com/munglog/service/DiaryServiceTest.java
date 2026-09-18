package com.munglog.service;

import com.munglog.dto.DiaryRequest;
import com.munglog.dto.DiaryResponse;
import com.munglog.entity.Diary;
import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import com.munglog.repository.DogRepository;
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

    @Autowired
    DogRepository dogRepository;

    @Test
    @DisplayName("로그인한 회원이 소유한 강아지를 지정해 일기를 만들면 해당 강아지의 일기가 된다")
    void createDiary_assignsOwnedDog() {
        // Given
        Member owner = memberRepository.save(Member.builder()
                .email("owner@munglog.com")
                .password("1234")
                .nickname("보호자")
                .build());
        Dog dog = dogRepository.save(Dog.builder()
                .member(owner)
                .name("멍이")
                .build());
        DiaryRequest.DiaryPageRequest pageRequest = new DiaryRequest.DiaryPageRequest(
                "http://example.com/image.jpg",
                "산책한 날",
                1
        );
        DiaryRequest request = new DiaryRequest(
                dog.getId(),
                LocalDate.of(2026, 9, 16),
                LocalTime.of(10, 30),
                "sunny",
                true,
                true,
                List.of(pageRequest)
        );

        // When
        Long savedDiaryId = diaryService.createDiary(request, owner.getEmail());

        // Then
        Diary savedDiary = diaryRepository.findById(savedDiaryId).orElseThrow();
        assertThat(savedDiary.getDog().getId()).isEqualTo(dog.getId());
    }

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

        Dog testDog = Dog.builder()
                .member(testMember)
                .name("멍이")
                .build();
        dogRepository.save(testDog);

        DiaryRequest request = new DiaryRequest(
                testDog.getId(),
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

    @Test
    @DisplayName("다른 회원의 강아지에는 일기를 작성할 수 없다")
    void createDiary_fails_forOtherMembersDog() {
        //Given
        Member loginMember = Member.builder()
                .email("test@munglong.com")
                .password("1234")
                .nickname("테스터1")
                .build();
        memberRepository.save(loginMember);

        Member dogOwner = Member.builder()
                .email("test1@munglong.com")
                .password("1234")
                .nickname("테스터2")
                .build();
        memberRepository.save(dogOwner);

        Dog testDog = Dog.builder()
                .member(dogOwner)
                .name("멍이")
                .build();
        dogRepository.save(testDog);

        DiaryRequest.DiaryPageRequest pageRequest = new DiaryRequest.DiaryPageRequest(
                "http://example.com/image.jpg",
                "테스트 일기 본문",
                1
        );

        DiaryRequest request = new DiaryRequest(
                testDog.getId(),
                LocalDate.of(2026, 9, 16),
                LocalTime.of(10, 30),
                "rainy",
                true,
                true,
                List.of(pageRequest)
        );

        // When & Then
        assertThatThrownBy(() ->
                diaryService.createDiary(request, loginMember.getEmail()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 강아지의 일기를 작성할 수 없습니다");
    }

    @Test
    @DisplayName("다른 회원의 강아지의 일기를 삭제할 수 없다")
    void deleteDiary_fails_forOtherMembersDog() {
        // Given
        Member loginMember = Member.builder()
                .email("loginMember@munglog.com")
                .password("1234")
                .nickname("로그인 사용자")
                .build();
        memberRepository.save(loginMember);

        Member dogOwner = Member.builder()
                .email("ownerMember@munglog.com")
                .password("1234")
                .nickname("강아지 주인")
                .build();
        memberRepository.save(dogOwner);

        Dog testDog = Dog.builder()
                .member(dogOwner)
                .name("토토")
                .build();
        dogRepository.save(testDog);

        Diary testDiary = Diary.createDiary(
                testDog,
                LocalDate.now(),
                LocalTime.now(),
                "rainy",
                true,
                true
        );
        Long savedDiaryId = diaryRepository.save(testDiary).getId();

        // When & Then
        assertThatThrownBy(() ->
                diaryService.deleteDiary(savedDiaryId,  loginMember.getEmail()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("본인의 강아지에 작성된 일기만 접근할 수 있습니다.");
    }

    @Test
    @DisplayName("다른 회원의 강아지의 일기를 수정할 수 없다")
    void updateDiaryEntirely_fails_forOtherMembersDog() {
        // Given
        Member loginMember = Member.builder()
                .email("loginMember@munglog.com")
                .password("1234")
                .nickname("로그인 유저")
                .build();
        memberRepository.save(loginMember);

        Member dogOwner = Member.builder()
                .email("dogOwner@munglog.com")
                .password("1234")
                .nickname("강아지 주인")
                .build();
        memberRepository.save(dogOwner);

        Dog testDog = Dog.builder()
                .member(dogOwner)
                .name("토토")
                .build();
        dogRepository.save(testDog);

        Diary testDiary = Diary.createDiary(
                testDog,
                LocalDate.now(),
                LocalTime.now(),
                "SUNNY",
                true,
                true
        );
        Long savedDiaryId = diaryRepository.save(testDiary).getId();

        DiaryRequest request = new DiaryRequest(
                testDog.getId(),
                LocalDate.now(),
                LocalTime.now(),
                "SUNNY",
                true,
                true,
                List.of()
        );

        assertThatThrownBy(() ->
                diaryService.updateDiaryEntirely(savedDiaryId, request, loginMember.getEmail()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("본인의 강아지에 작성된 일기만 접근할 수 있습니다.");
    }

    @Test
    @DisplayName("일기 전체 수정 시 날짜, 시간, 날씨도 변경된다")
    void updateDiaryEntirely_changesDateTimeAndWeather() {
        // Given
        Member dogOwner = Member.builder()
                .email("dogOwner@munglog.com")
                .password("1234")
                .nickname("토토주인")
                .build();
        memberRepository.save(dogOwner);

        Dog testDog = Dog.builder()
                .member(dogOwner)
                .name("토토")
                .build();
        dogRepository.save(testDog);

        Diary testDiary = Diary.createDiary(
                testDog,
                LocalDate.now(),
                LocalTime.now(),
                "SUNNY",
                true,
                true
        );

        diaryRepository.save(testDiary).getId();

        DiaryRequest updateRequest = new DiaryRequest(
                testDog.getId(),
                LocalDate.of(2026, 1, 1),
                LocalTime.of(0, 0, 0),
                "CLOUDY",
                true,
                true,
                List.of()
        );

        // When
        diaryService.updateDiaryEntirely(testDiary.getId(), updateRequest, dogOwner.getEmail());

        // Then
        Diary updatedDiary = diaryRepository.findById(testDiary.getId())
                .orElseThrow();

        assertThat(updatedDiary.getDiaryDate()).isEqualTo(updateRequest.date());
        assertThat(updatedDiary.getDiaryTime()).isEqualTo(updateRequest.time());
        assertThat(updatedDiary.getWeather()).isEqualTo(updateRequest.weather());
    }
}
