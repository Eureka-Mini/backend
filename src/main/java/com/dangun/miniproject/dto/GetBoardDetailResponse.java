package com.dangun.miniproject.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dangun.miniproject.domain.Board;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBoardDetailResponse {
	private Long id;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime createdAt;
	private List<GetCommentResponse> comments = new ArrayList<>();

	public static GetBoardDetailResponse from(final Board board) {
		final GetBoardDetailResponse boardResponse = new GetBoardDetailResponse();

		boardResponse.id = board.getId();
		boardResponse.title = board.getTitle();
		boardResponse.content = board.getContent();
		boardResponse.writer = board.getMember().getNickname();
		boardResponse.createdAt = board.getCreatedAt();

		return boardResponse;
	}
}
