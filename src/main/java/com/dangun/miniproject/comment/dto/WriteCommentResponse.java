package com.dangun.miniproject.comment.dto;

import lombok.Getter;

@Getter
public class WriteCommentResponse {
    private String content;

    public WriteCommentResponse(String content) {
        this.content = content;
    }
}
