package com.dangun.miniproject.comment.dto;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.domain.Member;
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
