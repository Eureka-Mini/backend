package com.dangun.miniproject.dto;

import lombok.Getter;

@Getter
public class UpdateCommentResponse {
    private String content;

    public UpdateCommentResponse(String content) {
        this.content = content;
    }
}