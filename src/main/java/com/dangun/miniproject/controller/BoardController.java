package com.dangun.miniproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangun.miniproject.dto.GetBoardDetailResponse;
import com.dangun.miniproject.dto.GetBoardResponse;
import com.dangun.miniproject.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

	private final BoardService boardService;

	// 게시글 상세 조회
	@GetMapping("/{boardId}")
	public GetBoardDetailResponse getBoardDetail(@PathVariable("boardId") final Long boardId) {
		return boardService.getBoardDetail(boardId);
	}

	// 게시글 목록 조회
	@GetMapping
	public Page<GetBoardResponse> getBoardList(
		@RequestParam(value = "keyword", required = false) final String keyword,
		final Pageable pageable
	) {
		if (keyword != null && !keyword.isEmpty()) {
			return boardService.getBoardList(keyword, pageable);
		}

		return boardService.getBoardList(pageable);
	}
}
