package com.dangun.miniproject.common.code;

import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CodeKeyTest {

	@Test
	@DisplayName("[성공] CodeKey 객체가 생성된다.")
	void init_codeKey_object() {
	    // given -- 테스트의 상태 설정

	    // when -- 테스트하고자 하는 행동
	    final CodeKey result = new CodeKey("010", "010");

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.getGroupCode()).isEqualTo("010");
			softAssertions.assertThat(result.getCode()).isEqualTo("010");
		});
	}

	@Test
	@DisplayName("[성공] CodeKey 기본 생성자로 객체 생성 시 필드 값이 null 이다.")
	void init_code_default_constructor() {
		// given -- 테스트의 상태 설정

		// when -- 테스트하고자 하는 행동
		final CodeKey result = new CodeKey();

		// then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions ->  {
			softAssertions.assertThat(result.getGroupCode()).isEqualTo(null);
			softAssertions.assertThat(result.getCode()).isEqualTo(null);
		});
	}
}