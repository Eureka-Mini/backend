package com.dangun.miniproject.controller;

import com.dangun.miniproject.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	// 작성 게시글 목록 조회
	@GetMapping("/my-board")
	public Page<GetBoardResponse> getMyBoardList(
			@RequestParam("memberId") final Long memberId,
			final Pageable pageable
	) {
		return boardService.getMyBoardList(memberId, pageable);
	}


	// 게시글 생성
	@PostMapping
	public ResponseEntity<BoardResponse> createBoard(@RequestBody CreateBoardRequest createBoardRequest) {
		BoardResponse createdBoard = boardService.createBoard(createBoardRequest);
		return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
	}

	// 게시글 수정
	@PutMapping("/{boardId}")
	public ResponseEntity<BoardResponse> updateBoard(
			@PathVariable Long boardId,
			@RequestBody UpdateBoardRequest updateBoardRequest
	) {
		BoardResponse updatedBoard = boardService.updateBoard(boardId, updateBoardRequest);
		return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
	}

	// 게시글 삭제
	@DeleteMapping("/{boardId}")
	public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
		boardService.deleteBoard(boardId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}