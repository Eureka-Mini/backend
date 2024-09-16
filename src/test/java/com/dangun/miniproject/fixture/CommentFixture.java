package com.dangun.miniproject.fixture;

import static java.util.concurrent.ThreadLocalRandom.*;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.domain.Member;

public class CommentFixture {

	public static Comment instanceOf(final Member member, final Board board) {

		final String content = "Comment Content" + current().nextInt(100, 1000);

		return Comment.builder()
			.content(content)
			.member(member)
			.board(board)
			.build();
	}
}
