package com.dangun.miniproject.board.dto;

import java.time.LocalDateTime;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;

import com.dangun.miniproject.common.code.CodeKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBoardResponse {
	private Long id;
	private String title;
	private String writer;
	private Integer price;
	private BoardStatus boardStatus;
	private CodeKey codeKey;
	private LocalDateTime createdAt;

	public static GetBoardResponse from(final Board board) {
		final GetBoardResponse boardResponse = new GetBoardResponse();

		boardResponse.id = board.getId();
		boardResponse.title = board.getTitle();
		boardResponse.writer = board.getMember().getNickname();
		boardResponse.price = board.getPrice();
		boardResponse.codeKey = board.getCodeKey();
//		boardResponse.boardStatus = board.getBoardStatus();
		boardResponse.createdAt = board.getCreatedAt();

		return boardResponse;
	}
}
