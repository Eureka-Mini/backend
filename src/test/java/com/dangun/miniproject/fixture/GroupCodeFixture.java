package com.dangun.miniproject.fixture;

import com.dangun.miniproject.common.code.GroupCode;

public class GroupCodeFixture {

	public static GroupCode instanceOf() {

		return new GroupCode("010", "판매상태", "판매 상태를 알리는 코드");
	}
}
