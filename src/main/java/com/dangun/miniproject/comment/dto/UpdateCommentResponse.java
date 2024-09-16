package com.dangun.miniproject.comment.dto;

import lombok.Getter;

@Getter
public class UpdateCommentResponse {
    private String content;

    public UpdateCommentResponse(String content) {
        this.content = content;
    }
}