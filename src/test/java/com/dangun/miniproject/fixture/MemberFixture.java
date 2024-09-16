package com.dangun.miniproject.fixture;

import com.dangun.miniproject.member.domain.Member;

import static java.util.concurrent.ThreadLocalRandom.current;

public class MemberFixture {

    public static Member instanceOf() {

        final String email = "email" + current().nextInt(100, 1000) + "@gmail.com";

        return Member.builder()
                .email(email)
                .nickname("nickname")
                .password("password")
                .build();
    }
}
