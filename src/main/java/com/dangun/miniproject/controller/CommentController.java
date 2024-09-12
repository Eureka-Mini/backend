package com.dangun.miniproject.controller;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;
import com.dangun.miniproject.service.CommentService;
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
    public ResponseEntity<?> writeComment(@AuthenticationPrincipal Member member,
                                          @RequestBody WriteCommentRequest request,
                                          @PathVariable Long boardId) {
        WriteCommentResponse writeCommentResponse = commentService.writeComment(member, boardId, request);

        if(request.getContent().isBlank()){
            return ApiResponse.badRequest("COMMENT-F001", "Content is blank");
        }

        return ApiResponse.created("", "COMMENT-S001", writeCommentResponse, "Create Success");
    }
}
