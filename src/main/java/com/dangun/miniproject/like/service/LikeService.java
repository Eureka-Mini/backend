package com.dangun.miniproject.like.service;

import com.dangun.miniproject.like.dto.RegisterLikeRequest;
import com.dangun.miniproject.like.dto.RegisterLikeResponse;
import com.dangun.miniproject.member.domain.Member;

public interface LikeService {
	// 좋아요 등록
	RegisterLikeResponse registerLike(final Member member, final RegisterLikeRequest request);

	// TODO: 자신이 좋아요 한 게시글 조회
}
