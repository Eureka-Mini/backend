package com.dangun.miniproject.member.domain;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.like.domain.Like;
import com.dangun.miniproject.member.dto.GetMemberDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Embedded
    private CodeKey codeKey;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private Set<Like> likes = new HashSet<>();

    @Builder
    private Member(String email, String nickname, String password, CodeKey codeKey) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.codeKey = codeKey;
    }

    public void updateMember(GetMemberDto getMemberDto) {
        this.nickname = getMemberDto.getNickname();
    }

    public void addAddress(Address address) {
        this.address = address;
        Address.builder().member(this).build();
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
