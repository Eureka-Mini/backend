package com.dangun.miniproject.board.service;

import com.dangun.miniproject.board.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

	// 게시글 상세 조회
	GetBoardDetailResponse getBoardDetail(final Long boardId);

	// 게시글 목록 조회
	Page<GetBoardResponse> getBoardList(final Pageable pageable);

	// 게시글 키워드 검색
	Page<GetBoardResponse> getBoardList(final String keyword, final Pageable pageable);

	// 작성 게시글 목록 조회
	Page<GetBoardResponse> getMyBoardList(final Long memberId, final Pageable pageable);

	// 게시글 생성
	WriteBoardResponse writeBoard(WriteBoardRequest request, Long memberId);

	// 게시글 수정
	UpdateBoardResponse updateBoard(Long boardId, UpdateBoardRequest request, Long memberId);

	// 게시글 삭제
	DeleteBoardResponse deleteBoard(Long boardId, Long memberId);

}
