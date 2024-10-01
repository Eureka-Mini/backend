package com.dangun.miniproject.member.dto;

import com.dangun.miniproject.common.code.CodeKey;
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

    @JsonProperty("codeKey")
    private CodeKey codeKey;

    @JsonProperty("address")
    private GetAddressDto address;

    @Builder
    public GetMemberDto(String email, String nickname, GetAddressDto address, CodeKey codeKey) {
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.codeKey = codeKey;
    }
}
