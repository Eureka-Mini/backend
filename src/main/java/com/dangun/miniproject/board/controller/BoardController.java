package com.dangun.miniproject.board.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.board.dto.BoardResponse;
import com.dangun.miniproject.board.dto.CreateBoardRequest;
import com.dangun.miniproject.board.dto.UpdateBoardRequest;
import com.dangun.miniproject.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

	private final BoardService boardService;

	// 게시글 상세 조회
	@GetMapping("/{boardId}")
	public ResponseEntity<?> getBoardDetail(@PathVariable("boardId") final Long boardId) {

		return ApiResponse.ok(
			 "BOARD-S002",
			boardService.getBoardDetail(boardId),
			"Board Read Success"
		);
	}

	// 게시글 목록 조회
	@GetMapping
	public ResponseEntity<?> getBoardList(
			@RequestParam(value = "keyword", required = false) final String keyword,
			final Pageable pageable
	) {
		if (keyword != null && !keyword.isEmpty()) {

			return ApiResponse.ok(
				"BOARD-S003",
				boardService.getBoardList(keyword, pageable),
				"Board Sorted List Success"
			);
		}

		return ApiResponse.ok(
			"BOARD-S003",
			boardService.getBoardList(pageable),
			"Board Sorted List Success"
		);
	}

	// 작성 게시글 목록 조회
	@GetMapping("/my-board")
	public ResponseEntity<?> getMyBoardList(
			@AuthenticationPrincipal(expression = "member") final Member member,
			final Pageable pageable
	) {

		return ApiResponse.ok(
			"BOARD-S004",
			boardService.getMyBoardList(member.getId(), pageable),
			"My Board List Success"
		);
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