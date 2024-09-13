package com.dangun.miniproject.dto;

import lombok.Getter;

@Getter
public class DeleteCommentResponse {
    private Long commentId;

    public DeleteCommentResponse(Long commentId) {
        this.commentId = commentId;
    }
}