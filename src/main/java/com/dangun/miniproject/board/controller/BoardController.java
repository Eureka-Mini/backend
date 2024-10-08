package com.dangun.miniproject.board.controller;

import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.board.dto.DeleteBoardResponse;
import com.dangun.miniproject.board.dto.UpdateBoardRequest;
import com.dangun.miniproject.board.dto.UpdateBoardResponse;
import com.dangun.miniproject.board.dto.WriteBoardRequest;
import com.dangun.miniproject.board.service.BoardService;
import com.dangun.miniproject.common.ApiResponse;
import com.dangun.miniproject.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
                    "BOARD-S002",
                    boardService.getBoardList(keyword, pageable),
                    "Board Sorted List Success"
            );
        }

        return ApiResponse.ok(
                "BOARD-S002",
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
                "BOARD-S002",
                boardService.getMyBoardList(member.getId(), pageable),
                "My Board List Success"
        );
    }

    // 본인이 좋아요 누른 게시판 조회
    @GetMapping("/board/like")
    public ResponseEntity<?> getMyBoardLikeList(
            @AuthenticationPrincipal(expression = "member") final Member member,
            final Pageable pageable
    ) {

        return ApiResponse.ok(
                "BOARD-S002",
                boardService.getMyBoardLikeList(member.getId(), pageable),
                "My Like Board List Success"
        );
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<?> writeBoard(
            @AuthenticationPrincipal(expression = "member") final Member member,
            @RequestBody final WriteBoardRequest writeBoardRequest
    ) {
        final Long memberId = member.getId();

        return ApiResponse.created(
                "",
                "BOARD-S001",
                boardService.writeBoard(writeBoardRequest, memberId),
                "Board Write Success"
        );
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<?> updateBoard(
            @AuthenticationPrincipal UserDetailsDto userDetailsDto,
            @PathVariable Long boardId,
            @RequestBody UpdateBoardRequest updateBoardRequest) {
        Long memberId = userDetailsDto.getMember().getId();
        UpdateBoardResponse response = boardService.updateBoard(boardId, updateBoardRequest, memberId);
        return ApiResponse.ok(
                "BOARD-S003",
                response,
                "Board Update Success"
        );
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<DeleteBoardResponse> deleteBoard(
            @AuthenticationPrincipal UserDetailsDto userDetailsDto,
            @PathVariable Long boardId) {
        Long memberId = userDetailsDto.getMember().getId();
        DeleteBoardResponse response = boardService.deleteBoard(boardId, memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}