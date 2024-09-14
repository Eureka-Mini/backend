package com.dangun.miniproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
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
}
