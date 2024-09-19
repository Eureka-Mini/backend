package com.dangun.miniproject.board.service.impl;

import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.board.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.comment.dto.GetCommentResponse;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.member.repository.MemberRepository;
import com.dangun.miniproject.board.service.BoardService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	/**
	 * 게시글 상세 조회
	 */
	@Override
	public GetBoardDetailResponse getBoardDetail(final Long boardId) {

		// TODO: ExceptionHandler 로 예외 처리 수정
		final Board board = boardRepository.findById(boardId)
				.orElseThrow(IllegalArgumentException::new);

		final GetBoardDetailResponse boardResponse = boardRepository.findBoardById(boardId);

		for (Comment comment : board.getComments()) {
			final boolean isBoardWriter = checkBoardWriter(board, comment);
			final GetCommentResponse commentResponse = GetCommentResponse.from(comment, isBoardWriter);

			boardResponse.getComments().add(commentResponse);
		}

		return boardResponse;
	}

	/**
	 * 게시글 목록 조회
	 */
	@Override
	public Page<GetBoardResponse> getBoardList(final Pageable pageable) {

		final PageRequest pageRequest = PageRequest.of(0, 10);
		final Page<Board> boards = boardRepository.findAllWithMember(pageRequest);

		return boards.map(GetBoardResponse::from);
	}

	/**
	 * 게시글 키워드 검색
	 */
	@Override
	public Page<GetBoardResponse> getBoardList(final String keyword, final Pageable pageable) {

		final PageRequest pageRequest = PageRequest.of(0, 10);
		final Page<Board> boards = boardRepository.searchBoardsByKeyword(keyword, pageRequest);

		return boards.map(GetBoardResponse::from);
	}

	/**
	 * 작성 게시글 목록 조회
	 */
	@Override
	public Page<GetBoardResponse> getMyBoardList(final Long memberId, final Pageable pageable) {

		final PageRequest pageRequest = PageRequest.of(0, 10);
		final Page<Board> boards = boardRepository.findAllByMyBoard(memberId, pageRequest);

		return boards.map(GetBoardResponse::from);
	}

	// 게시글 작성자인지 확인
	private boolean checkBoardWriter(final Board board, final Comment comment) {

		final Long boardWriter = board.getMember().getId();
		final Long commentWriter = comment.getMember().getId();

		return boardWriter.equals(commentWriter);
	}

	/**
	 * 게시글 생성
	 */
	@Override
	@Transactional
	public WriteBoardResponse writeBoard(WriteBoardRequest request, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("Member not found"));

		Board board = Board.builder()
				.title(request.getTitle())
				.content(request.getContent())
				.member(member)
				.boardStatus(BoardStatus.판매중)
				.price(request.getPrice())
				.build();

		Board savedBoard = boardRepository.save(board);

		return WriteBoardResponse.builder()
				.code("BOARD-S001")
				.message("Board Write Success")
				.data(WriteBoardResponse.BoardData.builder()
						.id(savedBoard.getId())
						.build())
				.timestamp(LocalDateTime.now())
				.build();
	}

	/**
	 * 게시글 수정
	 */
	@Override
	@Transactional
	public UpdateBoardResponse updateBoard(Long boardId, UpdateBoardRequest request, Long memberId) {
		if (memberId == null) {
			throw new RuntimeException("Token Not Exist");
		}

		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("Board not found"));

		if (!board.getMember().getId().equals(memberId)) {
			throw new RuntimeException("User Not Found");
		}

		// 부분 업데이트 로직
		//null이면 그 필드는 업데이트하지 않고 기존 값을 유지
		board.updateDetails(
				request.getTitle() != null ? request.getTitle() : board.getTitle(),
				request.getContent() != null ? request.getContent() : board.getContent(),
				request.getPrice() != null ? request.getPrice() : board.getPrice(),
				request.getBoardStatus() != null ? BoardStatus.valueOf(request.getBoardStatus()) : board.getBoardStatus()
		);

		// 응답 생성
		return UpdateBoardResponse.builder()
				.code("BOARD-S002")
				.message("Board Update Success")
				.data(UpdateBoardResponse.Data.builder()
						.content(board.getContent())
						.build())
				.timestamp(LocalDateTime.now())
				.build();
	}


	/**
	 * 게시글 삭제
	 */
	@Override
	@Transactional
	public DeleteBoardResponse deleteBoard(Long boardId, Long memberId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("Board not found"));

		if (!board.getMember().getId().equals(memberId)) {
			throw new RuntimeException("User Not Found");
		}

		boardRepository.delete(board);

		return DeleteBoardResponse.builder()
				.code("BOARD-S001")
				.message("Board Delete Success")
				.timestamp(LocalDateTime.now())
				.build();
	}
}
