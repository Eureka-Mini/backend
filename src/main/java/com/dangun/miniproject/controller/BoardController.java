package com.dangun.miniproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangun.miniproject.dto.GetBoardDetailResponse;
import com.dangun.miniproject.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

	private final BoardService boardService;

	@GetMapping("/{boardId}")
	public GetBoardDetailResponse getBoardDetail(@PathVariable("boardId") final Long boardId) {
		return boardService.getBoardDetail(boardId);
	}

}
