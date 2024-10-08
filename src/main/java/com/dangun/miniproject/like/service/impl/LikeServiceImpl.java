package com.dangun.miniproject.like.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.exception.BoardNotFoundException;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.like.domain.Like;
import com.dangun.miniproject.like.dto.RegisterLikeRequest;
import com.dangun.miniproject.like.dto.RegisterLikeResponse;
import com.dangun.miniproject.like.repository.LikeRepository;
import com.dangun.miniproject.like.service.LikeService;
import com.dangun.miniproject.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

	private final BoardRepository boardRepository;
	private final LikeRepository likeRepository;

	/**
	 * 좋아요 등록
	 */
	@Override
	@Transactional
	public RegisterLikeResponse registerLike(final Member member, final RegisterLikeRequest request) {
		final Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(BoardNotFoundException::new);

		final Like like = Like.builder()
			.member(member)
			.board(board)
			.build();

		likeRepository.save(like);

		return new RegisterLikeResponse(like.getId());
	}
}
