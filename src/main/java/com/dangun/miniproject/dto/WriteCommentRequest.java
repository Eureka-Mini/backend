package com.dangun.miniproject.dto;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import lombok.Getter;

@Getter
public class WriteCommentRequest {
    private String content;

    public Comment toEntity(Member member, Board board) {
        return Comment.builder()
                .board(board)
                .content(content)
                .member(member)
                .build();
    }
}
