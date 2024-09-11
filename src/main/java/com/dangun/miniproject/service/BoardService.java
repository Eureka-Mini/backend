package com.dangun.miniproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dangun.miniproject.dto.GetBoardDetailResponse;
import com.dangun.miniproject.dto.GetBoardResponse;

public interface BoardService {

	// 게시글 상세 조회
	GetBoardDetailResponse getBoardDetail(final Long boardId);

	// 게시글 목록 조회
	Page<GetBoardResponse> getBoardList(final Pageable pageable);

	// 게시글 키워드 검색
	Page<GetBoardResponse> getBoardList(final String keyword, final Pageable pageable);
}
