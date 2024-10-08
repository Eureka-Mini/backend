package com.dangun.miniproject.board.domain;

import static jakarta.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.domain.BaseEntity;
import com.dangun.miniproject.like.domain.Like;
import com.dangun.miniproject.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Integer price;

    @Embedded
    private CodeKey codeKey;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private Set<Like> likes = new HashSet<>();

    @Builder
    private Board(String content, Member member, Integer price, String title, CodeKey codeKey) {
        this.content = content;
        this.member = member;
        this.price = price;
        this.codeKey = codeKey;
        this.title = title;
    }

    // 수정 메서드 추가
    public void updateDetails(String title, String content, Integer price, CodeKey codeKey) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.codeKey = codeKey;
    }
}
