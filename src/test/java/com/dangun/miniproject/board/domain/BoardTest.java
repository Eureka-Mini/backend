package com.dangun.miniproject.board.domain;

import static org.assertj.core.api.SoftAssertions.*;

import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {

	@Test
	@DisplayName("[성공] Board 객체가 생성된다.")
	void init_board_object() {
		// given -- 테스트의 상태 설정
		final Member member = Member.builder()
			.email("test@test.com")
			.build();

		final CodeKey codeKey = new CodeKey(BoardStatus.판매중.getGroupId(), BoardStatus.판매완료.getCodeId());

		// when -- 테스트하고자 하는 행동
		final Board result = Board.builder()
			.title("title")
			.content("content")
			.price(10000)
			.codeKey(codeKey)
			.member(member)
			.build();

		// then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result).isNotNull();
			softAssertions.assertThat(result.getTitle()).isEqualTo("title");
			softAssertions.assertThat(result.getContent()).isEqualTo("content");
			softAssertions.assertThat(result.getPrice()).isEqualTo(10000);
			softAssertions.assertThat(result.getCodeKey().getGroupCode()).isEqualTo(codeKey.getGroupCode());
			softAssertions.assertThat(result.getCodeKey().getCode()).isEqualTo(codeKey.getCode());
			softAssertions.assertThat(result.getMember()).isEqualTo(member);
		});
	}
}
