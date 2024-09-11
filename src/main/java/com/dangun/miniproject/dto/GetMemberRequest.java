package com.dangun.miniproject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GetMemberRequest {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private GetAddressRequest address;

    @Builder
    public GetMemberRequest(Long id, String email, String password, String nickname, GetAddressRequest address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
    }
}
