package com.dangun.miniproject.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dangun.miniproject.domain.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBoardDetailResponse {
	private Long id;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime createdAt;
	private List<GetCommentResponse> comments = new ArrayList<>();

	public static GetBoardDetailResponse from(final Board board) {
		final GetBoardDetailResponse getBoardDetailResponse = new GetBoardDetailResponse();

		getBoardDetailResponse.id = board.getId();
		getBoardDetailResponse.title = board.getTitle();
		getBoardDetailResponse.content = board.getContent();
		getBoardDetailResponse.writer = board.getMember().getNickname();
		getBoardDetailResponse.createdAt = board.getCreatedAt();

		return getBoardDetailResponse;
	}
}
