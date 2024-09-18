package com.dangun.miniproject.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class GetMemberDto {
    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("address")
    private GetAddressDto address;

    @Builder
    public GetMemberDto(String email, String nickname, GetAddressDto address) {
        this.email = email;
        this.nickname = nickname;
        this.address = address;
    }

    public void GetMemberUpdateDto(String nickname) {
        this.nickname = nickname;
    }
}
