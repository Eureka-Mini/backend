package com.dangun.miniproject.common.code;

import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CodeTest {

	@Test
	@DisplayName("[성공] Code 객체가 생성된다.")
	void init_code_object() {
	    // given -- 테스트의 상태 설정
	    final CodeKey codeKey = new CodeKey("010", "010");

	    // when -- 테스트하고자 하는 행동
		final Code result = new Code(codeKey, "판매중", "sale", 1);

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.codeKey.getCode()).isEqualTo(codeKey.getCode());
			softAssertions.assertThat(result.codeKey.getGroupCode()).isEqualTo(codeKey.getGroupCode());
			softAssertions.assertThat(result.getCodeName()).isEqualTo("판매중");
			softAssertions.assertThat(result.getCodeNameBrief()).isEqualTo("sale");
			softAssertions.assertThat(result.getOrderNo()).isEqualTo(1);
		});
	}

	@Test
	@DisplayName("[성공] Code 기본 생성자로 객체 생성 시 필드 값이 null 이다.")
	void init_code_default_constructor() {
		// given -- 테스트의 상태 설정

		// when -- 테스트하고자 하는 행동
		final Code result = new Code();

		// then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions ->  {
			softAssertions.assertThat(result.codeKey).isEqualTo(null);
			softAssertions.assertThat(result.getCodeName()).isEqualTo(null);
			softAssertions.assertThat(result.getCodeNameBrief()).isEqualTo(null);
			softAssertions.assertThat(result.getOrderNo()).isEqualTo(0);
		});
	}

	@Test
	@DisplayName("[성공] CodeKey 는 setter 로 설정한다.")
	void set_codeKey() {
	    // given -- 테스트의 상태 설정
	    final Code result = new Code();
		final CodeKey codeKey = new CodeKey("010", "010");

	    // when -- 테스트하고자 하는 행동
	    result.setCodeKey(codeKey);

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.getCodeKey().getGroupCode()).isEqualTo(codeKey.getGroupCode());
			softAssertions.assertThat(result.getCodeKey().getCode()).isEqualTo(codeKey.getCode());
		});
	}
}
