package com.dangun.miniproject.comment.controller;

import com.dangun.miniproject.comment.dto.*;
import com.dangun.miniproject.comment.service.CommentService;
import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> writeComment(@AuthenticationPrincipal(expression = "member") Member member,
                                          @RequestBody WriteCommentRequest request,
                                          @PathVariable Long boardId) {

        if (request.getContent().isBlank()) {
            return ApiResponse.badRequest("COMMENT-F001", "Content is blank");
        }

        WriteCommentResponse writeCommentResponse = commentService.writeComment(member, boardId, request);

        return ApiResponse.created("", "COMMENT-S001", writeCommentResponse, "Create Success");
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@AuthenticationPrincipal(expression = "member") Member member,
                                           @RequestBody UpdateCommentRequest request,
                                           @PathVariable Long boardId,
                                           @PathVariable Long commentId) {
        UpdateCommentResponse response = commentService.updateComment(boardId, commentId, member, request);

        if (request.getContent().isBlank()) {
            return ApiResponse.badRequest("COMMENT-F001", "Content is blank");
        }

        return ApiResponse.ok("COMMENT-S002", response, "Update Success");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal(expression = "member") Member member,
                                           @PathVariable Long boardId,
                                           @PathVariable Long commentId) {
        commentService.deleteComment(boardId, commentId, member);

        return ApiResponse.ok("COMMENT-S003", new DeleteCommentResponse(commentId), "Delete Success");
    }
}
