package com.dangun.miniproject.domain;

import static jakarta.persistence.FetchType.*;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @ManyToOne(fetch = LAZY)
    private Board board;

    @Builder
    private Comment(Board board, String content, Member member) {
        this.board = board;
        this.content = content;
        this.member = member;
    }
}
