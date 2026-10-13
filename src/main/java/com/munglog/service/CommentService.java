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
                savedComment.getCreatedAt());
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
                comment.getCreatedAt()
        ));

        return responses;
    }
}
