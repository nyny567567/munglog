package com.munglog.controller;

import com.munglog.dto.CommentRequest;
import com.munglog.dto.CommentResponse;
import com.munglog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries/{diaryId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long diaryId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommentResponse response = commentService.createComment(diaryId, email, commentRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long diaryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();

        Page<CommentResponse> responses = commentService.getComments(diaryId, email, page, size);
        return ResponseEntity.ok(responses);
    }
}
