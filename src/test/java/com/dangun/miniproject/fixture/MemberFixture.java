package com.dangun.miniproject.fixture;

import com.dangun.miniproject.member.dto.GetAddressRequest;
import com.dangun.miniproject.member.dto.GetMemberRequest;

import static java.util.concurrent.ThreadLocalRandom.current;

public class MemberFixture {

    public static GetMemberRequest instanceOf() {

        final String email = "email" + current().nextInt(100, 1000) + "@gmail.com";

        return GetMemberRequest.builder()
                .email(email)
                .nickname("nickname")
                .password("password")
                .address(GetAddressRequest.builder().build())
                .build();
    }
}