package com.dangun.miniproject.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    @ManyToOne
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    private Board(String content, Member member, Integer price, BoardStatus boardStatus, String title) {
        this.content = content;
        this.member = member;
        this.price = price;
        this.boardStatus = boardStatus;
        this.title = title;
    }
}
