package com.munglog.service;

import com.munglog.dto.CommentRequest;
import com.munglog.dto.CommentResponse;
import com.munglog.entity.Diary;
import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import com.munglog.repository.CommentRepository;
import com.munglog.repository.DiaryRepository;
import com.munglog.repository.DogRepository;
import com.munglog.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("공개 일기에 댓글이 허용된 경우 댓글 작성에 성공한다")
    void createComment_success() {

        //Given
        Member diaryOwner = createMember("testMember@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary savedDiary = createDiary(testDog, true, true);

        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        CommentRequest request = new CommentRequest("좋은 일기네요!");

        // When
        CommentResponse response = commentService.createComment(
                savedDiary.getId(),
                commentAuthor.getEmail(),
                request
        );

        // Then
        assertThat(response.commentId()).isNotNull();
        assertThat(response.authorId()).isEqualTo(commentAuthor.getId());
        assertThat(response.nickname()).isEqualTo(commentAuthor.getNickname());
        assertThat(response.content()).isEqualTo(request.content());
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("공개 일기에 댓글이 비허용된 경우 댓글 작성에 실패한다")
    void createComment_fails_whenCommentNotAllowed() {

        //Given
        Member diaryOwner = createMember("testMember@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary savedDiary = createDiary(testDog, true, false);

        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        CommentRequest request = new CommentRequest("좋은 일기네요!");

        // When + Then
        assertThatThrownBy(() -> commentService.createComment(
                savedDiary.getId(),
                commentAuthor.getEmail(),
                request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글이 허용되지 않은 일기입니다.");
    }

    @Test
    @DisplayName("비공개 일기에 댓글이 허용된 경우 일기 작성자가 댓글 작성에 성공한다")
    void createComment_success_whenDiaryIsNotPublicAndCommentIsAllowed() {

        //Given
        Member diaryOwner = createMember("testMember@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary savedDiary = createDiary(testDog, false, true);

        CommentRequest request = new CommentRequest("좋은 일기네요!");

        // When
        CommentResponse response = commentService.createComment(
                savedDiary.getId(),
                diaryOwner.getEmail(),
                request
        );

        // Then
        assertThat(response.commentId()).isNotNull();
        assertThat(response.authorId()).isEqualTo(diaryOwner.getId());
        assertThat(response.nickname()).isEqualTo(diaryOwner.getNickname());
        assertThat(response.content()).isEqualTo(request.content());
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("댓글 허용인 비공개 일기에 일기 주인이 아닌 사용자가 댓글 작성을 시도하면 실패한다")
    void createComment_fails_whenNonOwnerCommentsOnPrivateDiary() {

        //Given
        Member diaryOwner = createMember("testMember@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary savedDiary = createDiary(testDog, false, true);

        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        CommentRequest request = new CommentRequest("좋은 일기네요!");

        // When + Then
        assertThatThrownBy(() -> commentService.createComment(
                savedDiary.getId(),
                commentAuthor.getEmail(),
                request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비공개 일기에는 댓글을 작성할 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 비허용인 비공개 일기에 일기 주인이 댓글 작성을 시도하면 실패한다")
    void createComment_fails_whenCommentsNotAllowed() {

        //Given
        Member diaryOwner = createMember("testMember@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary savedDiary = createDiary(testDog, false, false);

        CommentRequest request = new CommentRequest("좋은 일기네요!");

        // When + Then
        assertThatThrownBy(() -> commentService.createComment(
                savedDiary.getId(),
                diaryOwner.getEmail(),
                request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글이 허용되지 않은 일기입니다.");
    }

    private Member createMember(String email, String nickname) {
        return createMember(email, nickname, "SAMSONG");
    }

    private Member createMember(String email, String nickname, String regionCode) {
        Member testMember = Member.builder()
                .email(email)
                .password("1234")
                .nickname(nickname)
                .regionCode(regionCode)
                .build();
        return memberRepository.save(testMember);
    }

    private Dog createDog(Member member, String dogName) {
        Dog testDog = Dog.builder()
                .member(member)
                .name(dogName)
                .build();
        return dogRepository.save(testDog);
    }

    private Diary createDiary(Dog dog, boolean isPublic, boolean isCommentAllowed) {
        Diary createdDiary = Diary.createDiary(
                dog,
                LocalDate.of(2026, 10, 2),
                LocalTime.of(16, 47),
                "SUNNY",
                isPublic,
                isCommentAllowed
        );
        return diaryRepository.save(createdDiary);
    }

}
