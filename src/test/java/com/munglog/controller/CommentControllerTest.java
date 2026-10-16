package com.munglog.controller;

import com.munglog.entity.Comment;
import com.munglog.entity.Diary;
import com.munglog.entity.Dog;
import com.munglog.entity.Member;
import com.munglog.repository.CommentRepository;
import com.munglog.repository.DiaryRepository;
import com.munglog.repository.DogRepository;
import com.munglog.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("로그인한 회원이 댓글을 작성하면 200을 반환한다")
    void createComment_success() throws Exception {

        // Given
        Member dogOwner = createMember("owner@munglog.com", "토토누나");

        Dog dog = createDog(dogOwner, "토토");

        Diary savedDiary = createDiary(dog, true, true);

        String requestBody = """
                {
                    "content": "토토 너무 귀여워요!"
                }
                """;

        Authentication authentication = createAuthentication(dogOwner);

        // When
        MvcResult result = mockMvc.perform(post("/api/diaries/{diaryId}/comments", savedDiary.getId())
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("토토 너무 귀여워요!"))
                .andExpect(jsonPath("$.nickname").value("토토누나"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long commentId = jsonNode.get("commentId").asLong();

        Comment savedComment = commentRepository.findById(commentId).orElseThrow();

        assertEquals("토토 너무 귀여워요!", savedComment.getContent());
    }

    @Test
    @DisplayName("공개 일기의 댓글 목록을 조회하면 200을 반환한다")
    void getComments_success() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        createComment(diary, dogOwner, "토토 왜이렇게 뚱뚱해요?");
        createComment(diary, dogOwner, "토토 사랑스러워요.");

        // When & Then
        mockMvc.perform(get("/api/diaries/{diaryId}/comments", diary.getId())
                        .with(authentication(createAuthentication(dogOwner))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].content").value("토토 왜이렇게 뚱뚱해요?"))
                .andExpect(jsonPath("$.content[1].content").value("토토 사랑스러워요."));
    }

    @Test
    @DisplayName("다른 회원의 비공개 일기 댓글을 조회하면 403을 반환한다")
    void getComments_failsWhenNonOwnerAccessesPrivateDiary() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member viewer = createMember("viewer@munglog.com", "구경꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, false, true);

        //When & Then
        mockMvc.perform(get("/api/diaries/{diaryId}/comments", diary.getId())
                        .with(authentication(createAuthentication(viewer))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("일기 소유자가 자신의 비공개 일기 댓글을 조회하면 200을 반환한다")
    void getComments_successWhenOwnerAccessesPrivateDiary() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, false, true);
        createComment(diary, dogOwner, "토토 보고싶다");

        // When & Then
        mockMvc.perform(get("/api/diaries/{diaryId}/comments", diary.getId())
                        .with(authentication(createAuthentication(dogOwner))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].content").value("토토 보고싶다"));
    }

    @Test
    @DisplayName("댓글 작성이 허용되지 않은 일기에 댓글을 작성하면 403을 반환한다")
    void createComment_failsWhenCommentsNotAllowed() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member viewer = createMember("viewer@munglog.com", "구경꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, false);
        String requestBody = """
                {
                    "content": "토토 똥개"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/diaries/{diaryId}/comments", diary.getId())
                        .with(authentication(createAuthentication(viewer)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        assertThat(commentRepository.count()).isZero();
    }

    @Test
    @DisplayName("다른 회원의 비공개 일기에 댓글을 작성하면 403을 반환한다")
    void createComment_failsWhenPrivateDiary() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member viewer = createMember("viewer@munglog.com", "구경꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, false, true);
        String requestBody = """
                {
                    "content": "토토 바보"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/diaries/{diaryId}/comments", diary.getId())
                        .with(authentication(createAuthentication(viewer)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        assertThat(commentRepository.count()).isZero();
    }

    @Test
    @DisplayName("댓글 작성자가 자신의 댓글을 삭제하면 204를 반환한다")
    void deleteComment_successWhenCommentAuthor() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 돼지");

        // When & Then
        mockMvc.perform(delete("/api/diaries/{diaryId}/comments/{commentId}", diary.getId(), comment.getId())
                        .with(authentication(createAuthentication(commentAuthor))))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("일기 소유자가 댓글을 삭제하면 204를 반환한다")
    void deleteComment_successWhenDiaryOwner() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 돼지");

        // When & Then
        mockMvc.perform(delete("/api/diaries/{diaryId}/comments/{commentId}", diary.getId(), comment.getId())
                        .with(authentication(createAuthentication(dogOwner))))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("댓글 작성자도 일기 소유자도 아닌 회원이 삭제하면 403을 반환한다")
    void deleteComment_failsWhenNeitherDiaryOwnerNorCommentAuthor() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글꾼");
        Member viewer = createMember("viewer@munglog.com", "구경꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 돼지");

        // When & Then
        mockMvc.perform(delete("/api/diaries/{diaryId}/comments/{commentId}", diary.getId(), comment.getId())
                        .with(authentication(createAuthentication(viewer))))
                .andExpect(status().isForbidden());

        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @Test
    @DisplayName("댓글이 속하지 않은 일기에서 삭제를 요청하면 400을 반환한다")
    void deleteComment_failsWhenDiaryIdMismatch() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary1 = createDiary(dog, true, true);
        Diary diary2 = createDiary(dog, true, true);
        Comment comment = createComment(diary1, dogOwner, "zzzz");

        // When & Then
        mockMvc.perform(delete("/api/diaries/{diaryId}/comments/{commentId}", diary2.getId(), comment.getId())
                        .with(authentication(createAuthentication(dogOwner))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 일기의 댓글이 아닙니다."));

        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @Test
    @DisplayName("비로그인 사용자가 삭제를 요청하면 401을 반환한다")
    void deleteComment_failsWhenUnauthenticated() throws Exception {

        // Given
        Member dogOwner = createMember("dogOwner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, dogOwner, "zzzz");

        // When & Then
        mockMvc.perform(delete("/api/diaries/{diaryId}/comments/{commentId}", diary.getId(), comment.getId()))
                .andExpect(status().isUnauthorized());

        entityManager.flush();
        entityManager.clear();
        assertThat(commentRepository.findById(comment.getId())).isPresent();
    }

    @Test
    @DisplayName("댓글 작성자가 댓글을 수정하면 200을 반환한다")
    void updateComment_successWhenCommentAuthor() throws Exception {

        // Given
        Member dogOwner = createMember("owner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, dogOwner, "토토 못생겼어");
        Authentication authentication = createAuthentication(dogOwner);
        String requestBody = """
                {
                    "content": "토토 예쁘다!"
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/diaries/{diaryId}/comments/{commentId}",
                        diary.getId(),
                        comment.getId())
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("토토 예쁘다!"))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("댓글 작성자가 아닌 일기 주인이 댓글을 수정하면 403을 반환한다")
    void updateComment_failsWhenDiaryOwnerNotCommentAuthor() throws Exception {

        // Given
        Member dogOwner = createMember("owner@munglog.com", "토토누나");
        Member commentAuthor = createMember("commentAuthor@munglog.com", "댓글꾼");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, commentAuthor, "토토 왜이렇게 뚱뚱해요");
        Authentication authentication = createAuthentication(dogOwner);
        String requestBody = """
                {
                    "content": "토토 날씬하다!"
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/diaries/{diaryId}/comments/{commentId}",
                        diary.getId(),
                        comment.getId())
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 수정 요청의 내용이 공백만 있으면 400을 반환한다")
    void updateComment_failsWhenContentIsBlank() throws Exception {

        // Given
        Member dogOwner = createMember("owner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, dogOwner, "토토야");
        Authentication authentication = createAuthentication(dogOwner);
        String requestBody = """
                {
                    "content": "              "
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/diaries/{diaryId}/comments/{commentId}",
                        diary.getId(),
                        comment.getId())
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 수정 요청의 내용이 500자를 초과하면 400을 반환한다")
    void updateComment_failsWhenContentOver500() throws Exception {

        // Given
        Member dogOwner = createMember("owner@munglog.com", "토토누나");
        Dog dog = createDog(dogOwner, "토토");
        Diary diary = createDiary(dog, true, true);
        Comment comment = createComment(diary, dogOwner, "토토야");
        Authentication authentication = createAuthentication(dogOwner);
        String longContent = "토".repeat(501);

        String requestBody = """
                {
                    "content": "%s"
                }
                """.formatted(longContent);

        // When & Then
        mockMvc.perform(patch("/api/diaries/{diaryId}/comments/{commentId}",
                        diary.getId(),
                        comment.getId())
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    private Member createMember(String email, String nickname) {

        Member testMember = Member.builder()
                .email(email)
                .password("1234")
                .nickname(nickname)
                .regionCode("SAMSONG")
                .build();

        return memberRepository.save(testMember);
    }

    private Dog createDog(Member member, String name) {
        Dog testDog = Dog.builder()
                .member(member)
                .name(name)
                .build();

        return dogRepository.save(testDog);
    }

    private Diary createDiary(Dog savedDog, boolean isPublic, boolean isCommentAllowed) {
        Diary testDiary = Diary.createDiary(
                savedDog,
                LocalDate.of(2026, 10, 8),
                LocalTime.of(18, 20),
                "SUNNY",
                isPublic,
                isCommentAllowed
        );

        return diaryRepository.save(testDiary);
    }

    private Comment createComment(Diary diary, Member member, String content) {
        Comment testComment = Comment.createComment(diary, member, content);
        return commentRepository.save(testComment);
    }

    private static Authentication createAuthentication(Member member) {
        return new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
