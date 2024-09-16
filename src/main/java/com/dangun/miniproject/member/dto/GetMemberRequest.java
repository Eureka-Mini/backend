package com.dangun.miniproject.member.dto;

import com.dangun.miniproject.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMemberRequest {
    private String email;
    private String password;
    private String nickname;
    private GetAddressRequest address;

    public Member toEntity() {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        member.addAddress(address.toEntity(member));
        return member;
    }
}
