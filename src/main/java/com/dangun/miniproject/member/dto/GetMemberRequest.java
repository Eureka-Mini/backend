package com.dangun.miniproject.member.dto;

import com.dangun.miniproject.member.domain.Member;
import lombok.*;

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
                .nickname(nickname)
                .password(password)
                .build();

        if (address != null) {
            member.addAddress(address.toEntity(member));
        } else {
            throw new RuntimeException("Signup address null");
        }

        return member;
    }
}
