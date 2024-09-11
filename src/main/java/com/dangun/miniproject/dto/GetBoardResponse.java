package com.dangun.miniproject.dto;

import java.time.LocalDateTime;

import com.dangun.miniproject.domain.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBoardResponse {
	private Long id;
	private String title;
	private String writer;
	private LocalDateTime createdAt;

	public static GetBoardResponse from(final Board board) {
		final GetBoardResponse boardResponse = new GetBoardResponse();

		boardResponse.id = board.getId();
		boardResponse.title = board.getTitle();
		boardResponse.writer = board.getMember().getNickname();
		boardResponse.createdAt = board.getCreatedAt();

		return boardResponse;
	}
}
