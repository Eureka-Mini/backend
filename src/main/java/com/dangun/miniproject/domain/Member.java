package com.dangun.miniproject.domain;

import com.dangun.miniproject.dto.GetMemberRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "member")
    private List<Board> boards;

    @Builder
    private Member(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public void updateMember(GetMemberRequest getMemberRequest){
        this.email = getMemberRequest.getEmail();
        this.password = getMemberRequest.getPassword();
        this.nickname = getMemberRequest.getNickname();
    }
}
