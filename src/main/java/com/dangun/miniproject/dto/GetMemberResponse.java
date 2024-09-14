package com.dangun.miniproject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GetMemberResponse {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private GetAddressResponse address;

    @Builder
    public GetMemberResponse(Long id, String email, String password, String nickname, GetAddressResponse address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
    }

}
