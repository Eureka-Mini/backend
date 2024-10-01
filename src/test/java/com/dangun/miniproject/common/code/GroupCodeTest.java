package com.dangun.miniproject.common.code;

import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GroupCodeTest {

	@Test
	@DisplayName("[성공] GroupCode 객체가 생성된다.")
	void init_groupCode_object() {
	    // given -- 테스트의 상태 설정

	    // when -- 테스트하고자 하는 행동
		final GroupCode result = new GroupCode("010", "판매 상태", "판매 상태를 알리는 코드");

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.getGroupCode()).isEqualTo("010");
			softAssertions.assertThat(result.getGroupCodeName()).isEqualTo("판매 상태");
			softAssertions.assertThat(result.getGroupCodeDesc()).isEqualTo("판매 상태를 알리는 코드");
		});
	}

	@Test
	@DisplayName("[성공] GroupCode 기본 생성자로 객체 생성 시 필드 값이 null 이다.")
	void init_groupCode_default_constructor() {
	    // given -- 테스트의 상태 설정

	    // when -- 테스트하고자 하는 행동
		final GroupCode result = new GroupCode();

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.getGroupCode()).isEqualTo(null);
			softAssertions.assertThat(result.getGroupCodeName()).isEqualTo(null);
			softAssertions.assertThat(result.getGroupCodeDesc()).isEqualTo(null);
		});
	}
}