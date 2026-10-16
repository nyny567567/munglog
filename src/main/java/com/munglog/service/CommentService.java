package com.munglog.service;

import com.munglog.dto.CommentRequest;
import com.munglog.dto.CommentResponse;
import com.munglog.entity.Comment;
import com.munglog.entity.Diary;
import com.munglog.entity.Member;
import com.munglog.exception.CommentNotAllowedException;
import com.munglog.repository.CommentRepository;
import com.munglog.repository.DiaryRepository;
import com.munglog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public CommentResponse createComment(
            Long diaryId,
            String loginEmail,
            CommentRequest request) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다.")
                );
        Member commentAuthor = memberRepository.findByEmail(loginEmail)
                .orElseThrow(
                        () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );

        if (!diary.isCommentAllowed()) {
            throw new CommentNotAllowedException("댓글이 허용되지 않은 일기입니다.");
        }

        if (!diary.isPublic()
                && !diary.getDog().getMember().getId().equals(commentAuthor.getId())) {
            throw new AccessDeniedException("비공개 일기에는 댓글을 작성할 수 없습니다.");
        }

        Comment comment = Comment.createComment(diary, commentAuthor, request.content());
        Comment savedComment = commentRepository.save(comment);

        CommentResponse response = new CommentResponse(
                savedComment.getId(),
                savedComment.getMember().getId(),
                savedComment.getMember().getNickname(),
                savedComment.getContent(),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt());
        return response;

    }

    public Page<CommentResponse> getComments(
            Long diaryId,
            String loginEmail,
            int page,
            int size
    ) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 일기를 찾을 수 없습니다.")
                );
        Member commentViewer = memberRepository.findByEmail(loginEmail)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다.")
                );

        if (!diary.isPublic()
                && !diary.getDog().getMember().getId().equals(commentViewer.getId())) {
            throw new AccessDeniedException("비공개 일기의 댓글을 조회할 수 없습니다.");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "createdAt", "id")
        );

        Page<Comment> comments = commentRepository.findByDiaryId(diaryId, pageable);
        Page<CommentResponse> responses = comments.map(comment -> new CommentResponse(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        ));

        return responses;
    }

    @Transactional
    public void deleteComment(Long diaryId, Long commentId, String loginEmail) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다.")
                );

        if (!comment.getDiary().getId().equals(diaryId)) {
            throw new IllegalArgumentException("해당 일기의 댓글이 아닙니다.");
        }

        Member loginMember = memberRepository.findByEmail(loginEmail)
                .orElseThrow(
                        () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );

        boolean isCommentAuthor = comment.getMember().getId()
                .equals(loginMember.getId());

        boolean isDiaryOwner = comment.getDiary().getDog().getMember().getId()
                .equals(loginMember.getId());

        if (!isCommentAuthor && !isDiaryOwner) {
            throw new AccessDeniedException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long diaryId, Long commentId, String loginEmail, String content) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다.")
                );

        if (!comment.getDiary().getId().equals(diaryId)) {
            throw new IllegalArgumentException("해당 일기의 댓글이 아닙니다.");
        }

        Member member = memberRepository.findByEmail(loginEmail)
                .orElseThrow(
                        () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );

        boolean isCommentAuthor = comment.getMember().getId().equals(member.getId());

        if (!isCommentAuthor) {
            throw new AccessDeniedException("해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.updateContent(content);

        return new CommentResponse(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
