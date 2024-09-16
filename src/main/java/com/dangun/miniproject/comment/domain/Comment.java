package com.dangun.miniproject.comment.domain;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.common.domain.BaseEntity;
import com.dangun.miniproject.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    private Comment(Board board, String content, Member member) {
        this.board = board;
        this.content = content;
        this.member = member;
    }

    public void updateContent(String updatedContent) {
        this.content = updatedContent;
    }
}
