package com.dangun.miniproject.member.domain;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.dto.GetMemberDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    private Member(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public void updateMember(GetMemberDto getMemberRequest) {
        this.email = getMemberRequest.getEmail();
        this.nickname = getMemberRequest.getNickname();
    }

    public void addAddress(Address address) {
        this.address = address;
        Address.builder().member(this).build();
    }
}
