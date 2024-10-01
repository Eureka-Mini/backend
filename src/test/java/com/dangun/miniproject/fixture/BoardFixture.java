package com.dangun.miniproject.fixture;

import static java.util.concurrent.ThreadLocalRandom.*;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.member.domain.Member;

public class BoardFixture {

	public static Board instanceOf(final Member member) {

		final String title = "Test Title " + current().nextInt(100, 1000);
		final String content = "Test Content " + current().nextInt(100, 1000);
		final int price = current().nextInt(5000, 50000);
		final BoardStatus status = current().nextBoolean() ? BoardStatus.판매중 : BoardStatus.판매완료;

		return Board.builder()
			.title(title)
			.content(content)
			.price(price)
			.codeKey(new CodeKey("010", "010"))
			.member(member)
			.build();
	}

	public static Board instanceOf(final Member member, final String title, final String content) {

		final int price = current().nextInt(5000, 50000);
		final BoardStatus status = current().nextBoolean() ? BoardStatus.판매중 : BoardStatus.판매완료;

		return Board.builder()
			.title(title)
			.content(content)
			.price(price)
			.codeKey(new CodeKey("010", "010"))
			.member(member)
			.build();
	}
}
