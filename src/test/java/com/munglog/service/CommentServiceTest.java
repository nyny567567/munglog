package com.munglog.service;

import com.munglog.dto.CommentRequest;
import com.munglog.dto.CommentResponse;
import com.munglog.entity.Comment;
import com.munglog.entity.Diary;
import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import com.munglog.exception.CommentNotAllowedException;
import com.munglog.repository.CommentRepository;
import com.munglog.repository.DiaryRepository;
import com.munglog.repository.DogRepository;
import com.munglog.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

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
                .isInstanceOf(CommentNotAllowedException.class)
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
                .isInstanceOf(AccessDeniedException.class)
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
                .isInstanceOf(CommentNotAllowedException.class)
                .hasMessage("댓글이 허용되지 않은 일기입니다.");
    }

    @Test
    @DisplayName("공개 일기의 댓글 목록을 조회한다")
    void getComments_success() {

        // Given
        Member diaryOwner = createMember("diaryOwner@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary testDiary = createDiary(testDog, true, true);

        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글 작성자");
        Comment comment1 = createComment(testDiary, commentAuthor, "첫번째 댓글");
        Comment comment2 = createComment(testDiary, commentAuthor, "두번째 댓글");

        Member viewer = createMember("viewer@munglog.com", "구경꾼");

        // When
        Page<CommentResponse> commentPage = commentService.getComments(
                testDiary.getId(),
                viewer.getEmail(),
                0,
                10
        );

        // Then
        assertThat(commentPage.getTotalElements()).isEqualTo(2);
        assertThat(commentPage.getContent())
                .extracting(CommentResponse::content)
                .containsExactlyInAnyOrder("첫번째 댓글", "두번째 댓글");
    }

    @Test
    @DisplayName("다른 회원의 비공개 일기 댓글은 조회할 수 없다")
    void getComments_failsWhenNonOwnerAccessesPrivateDiary() {

        // Given
        Member diaryOwner = createMember("diaryOwner@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary testDiary = createDiary(testDog, false, true);

        Member viewer = createMember("viewer@munglog.com", "구경꾼");

        // When & Then
        assertThatThrownBy(() -> commentService.getComments(
                testDiary.getId(),
                viewer.getEmail(),
                0,
                10
        ))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("비공개 일기의 댓글을 조회할 수 없습니다.");
    }

    @Test
    @DisplayName("댓글을 작성 시각 오름차순으로 조회한다")
    void getComments_sortByCreatedAtAsc() {

        // Given
        Member diaryOwner = createMember("diaryOwner@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary testDiary = createDiary(testDog, true, true);

        Comment commentA = createComment(testDiary, diaryOwner, "댓글 A");
        Comment commentB = createComment(testDiary, diaryOwner, "댓글 B");
        Comment commentC = createComment(testDiary, diaryOwner, "댓글 C");

        entityManager.flush();

        jdbcTemplate.update(
                "UPDATE comment SET created_at = ? WHERE id = ?",
                Timestamp.valueOf(LocalDateTime.of(2026, 10, 8, 14, 30)),
                commentA.getId()
        );

        jdbcTemplate.update(
                "UPDATE comment SET created_at = ? WHERE id = ?",
                Timestamp.valueOf(LocalDateTime.of(2026, 10, 8, 14, 10)),
                commentB.getId()
        );

        jdbcTemplate.update(
                "UPDATE comment SET created_at = ? WHERE id = ?",
                Timestamp.valueOf(LocalDateTime.of(2026, 10, 8, 14, 20)),
                commentC.getId()
        );

        entityManager.clear();

        // When
        Page<CommentResponse> responses = commentService.getComments(
                testDiary.getId(),
                diaryOwner.getEmail(),
                0,
                10
        );

        // Then
        assertThat(responses.getContent())
                .extracting(CommentResponse::content)
                .containsExactly(
                        "댓글 B",
                        "댓글 C",
                        "댓글 A"
                );
    }

    @Test
    @DisplayName("댓글 목록을 페이징하여 조회한다")
    void getComments_pagination() {

        // Given
        Member diaryOwner = createMember("diaryOwner@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary testDiary = createDiary(testDog, true, true);

        Comment commentA = createComment(testDiary, diaryOwner, "댓글 A");
        Comment commentB = createComment(testDiary, diaryOwner, "댓글 B");
        Comment commentC = createComment(testDiary, diaryOwner, "댓글 C");
        Comment commentD = createComment(testDiary, diaryOwner, "댓글 D");
        Comment commentE = createComment(testDiary, diaryOwner, "댓글 E");

        // When - 첫 번째 페이지 조회
        Page<CommentResponse> responses = commentService.getComments(
                testDiary.getId(), diaryOwner.getEmail(), 0, 2
        );

        // Then - 첫 번째 페이지 검증
        assertThat(responses.getTotalElements()).isEqualTo(5);
        assertThat(responses.getTotalPages()).isEqualTo(3);
        assertThat(responses.getNumberOfElements()).isEqualTo(2);

        // When - 마지막 페이지 조회
        Page<CommentResponse> lastPage = commentService.getComments(
                testDiary.getId(), diaryOwner.getEmail(), 2, 2
        );

        // Then - 마지막 페이지 검증
        assertThat(lastPage.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("비공개 일기의 주인은 댓글을 조회할 수 있다")
    void getComments_successWhenOwnerAccessesPrivateDiary() {

        // Given
        Member diaryOwner = createMember("diaryOwner@munglog.com", "토토누나");
        Dog testDog = createDog(diaryOwner, "토토");
        Diary testDiary = createDiary(testDog, false, true);
        Comment commentA = createComment(testDiary, diaryOwner, "댓글 A");

        // When
        Page<CommentResponse> responses = commentService.getComments(
                testDiary.getId(), diaryOwner.getEmail(), 0, 10
        );

        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getContent())
                .extracting(CommentResponse::content)
                .containsExactly("댓글 A");
    }

    @Test
    @DisplayName("댓글 작성자 본인은 댓글을 삭제할 수 있다")
    void deleteComment_successWhenCommentAuthor() {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 못생겼어요");

        // When
        commentService.deleteComment(diary.getId(), comment.getId(), commentAuthor.getEmail());

        // Then
        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("일기 작성자는 다른 사람의 댓글을 삭제할 수 있다")
    void deleteComment_successWhenDiaryOwner() {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 왜이렇게 뚱뚱해요?");

        // When
        commentService.deleteComment(diary.getId(), comment.getId(), dogOwner.getEmail());

        // Then
        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("댓글 작성자도 일기 작성자도 아닌 회원은 댓글을 삭제할 수 없다")
    void deleteComment_failsWhenNeitherCommentAuthorNorDiaryOwner() {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글주인");
        Member viewer = createMember("viewer@munglog.com", "나그네");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "돈이 없어요");

        // When & Then
        assertThatThrownBy(() ->
                commentService.deleteComment(diary.getId(), comment.getId(), viewer.getEmail())
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 댓글을 삭제할 권한이 없습니다.");

        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다")
    void deleteComment_failsWhenCommentNotFound() {

        // Given
        Member loginMember = createMember("loginMember@munglog.com", "로그인 유저");
        Long nonexistentDiaryId = 999998L;
        Long nonexistentCommentId = 999999L;

        // When & Then
        assertThatThrownBy(() ->
                commentService.deleteComment(nonexistentDiaryId, nonexistentCommentId, loginMember.getEmail())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 댓글을 찾을 수 없습니다.");
    }


    @Test
    @DisplayName("댓글이 속하지 않은 일기에서는 댓글을 삭제할 수 없다")
    void deleteComment_failsWhenDiaryIdMismatch() {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary1 = createDiary(dog, true, true);
        Diary diary2 = createDiary(dog, true, true);
        Comment comment = createComment(diary1, dogOwner, "토미야");

        // When & Then
        assertThatThrownBy(() ->
                commentService.deleteComment(diary2.getId(), comment.getId(), dogOwner.getEmail())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 일기의 댓글이 아닙니다.");

        assertThat(commentRepository.findById(comment.getId())).isPresent();
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

    private Comment createComment(Diary diary, Member commentAuthor, String content) {
        Comment comment = Comment.createComment(
                diary,
                commentAuthor,
                content
        );

        return commentRepository.save(comment);

    }

}
