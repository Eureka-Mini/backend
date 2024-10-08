package com.dangun.miniproject.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.like.dto.RegisterLikeRequest;
import com.dangun.miniproject.like.dto.RegisterLikeResponse;
import com.dangun.miniproject.like.service.LikeService;
import com.dangun.miniproject.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

	private final LikeService likeService;

	// 좋아요 등록
	@PostMapping
	public ResponseEntity<?> registerLike(
		@AuthenticationPrincipal(expression = "member") final Member member,
		@RequestBody final RegisterLikeRequest request
	) {
		final RegisterLikeResponse response = likeService.registerLike(member, request);

		return ApiResponse.created(
			"",
			"LIKE-S001",
			response,
			"Like Register"
		);
	}
}
