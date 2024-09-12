package com.dangun.miniproject.service;

import com.dangun.miniproject.dto.*;
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
	BoardResponse createBoard(CreateBoardRequest createBoardRequest);

	// 게시글 수정
	BoardResponse updateBoard(Long id, UpdateBoardRequest updateBoardRequest);

	// 게시글 삭제
	void deleteBoard(Long boardId);

}
