package com.dangun.miniproject.like.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterLikeResponse {
	private Long id;

	public RegisterLikeResponse(final Long id) {
		this.id = id;
	}
}
