package com.dangun.miniproject.board.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.board.dto.BoardResponse;
import com.dangun.miniproject.board.dto.CreateBoardRequest;
import com.dangun.miniproject.board.dto.GetBoardDetailResponse;
import com.dangun.miniproject.board.dto.GetBoardResponse;
import com.dangun.miniproject.comment.dto.GetCommentResponse;
import com.dangun.miniproject.board.dto.UpdateBoardRequest;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.member.repository.MemberRepository;
import com.dangun.miniproject.board.service.BoardService;

import lombok.RequiredArgsConstructor;

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
	public BoardResponse createBoard(CreateBoardRequest request) {
		Member member = (Member) memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new RuntimeException("회원을 찾을 수 없음"));

		Board board = Board.builder()
				.content(request.getContent())
				.member(member)
				.price(request.getPrice())
				.boardStatus(request.getBoardStatus())
				//.boardStatus(BoardStatus.valueOf(request.getBoardStatus()))
				.title(request.getTitle())
				.build();

		Board savedBoard = boardRepository.save(board);
		return convertToResponse(savedBoard);
	}

	/**
	 * 게시글 수정
	 */
	@Override
	@Transactional
	public BoardResponse updateBoard(Long boardId, UpdateBoardRequest request) {
		Board existingBoard = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("게시글 찾을 수 없음"));

		existingBoard.updateDetails(request.getTitle(), request.getContent(), request.getPrice(), request.getBoardStatus());

		Board updatedBoard = boardRepository.save(existingBoard);
		return convertToResponse(updatedBoard);
	}

	/**
	 * 게시글 삭제
	 */
	@Transactional
	public void deleteBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("게시글 찾을 수 없음"));

		boardRepository.delete(board);
	}

	private BoardResponse convertToResponse(Board board) {
		return new BoardResponse(
				board.getId(),
				board.getTitle(),
				board.getContent(),
				board.getPrice(),
				board.getBoardStatus(),
				board.getMember().getId()
		);
	}
}
