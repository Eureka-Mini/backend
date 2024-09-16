package com.dangun.miniproject.comment.dto;

import java.time.LocalDateTime;

import com.dangun.miniproject.comment.domain.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetCommentResponse {
	private Long id;	// comment ID
	private String content;
	private Long writerId;
	private String writer;
	private boolean isBoardWriter;
	private LocalDateTime createdAt;

	public static GetCommentResponse from(final Comment comment, final boolean isBoardWriter) {
		final GetCommentResponse commentResponse = new GetCommentResponse();

		commentResponse.id = comment.getId();
		commentResponse.content = comment.getContent();
		commentResponse.writerId = comment.getMember().getId();
		commentResponse.writer = comment.getMember().getNickname();
		commentResponse.isBoardWriter = isBoardWriter;
		commentResponse.createdAt = comment.getCreatedAt();

		return commentResponse;
	}
}
