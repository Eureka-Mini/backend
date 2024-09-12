package com.dangun.miniproject.fixture;

import com.dangun.miniproject.domain.Member;

public class MemberFixture {

	public static Member instanceOf() {

		return Member.builder()
			.email("oi@test.com")
			.nickname("oi")
			.password("oi5252")
			.build();
	}
}