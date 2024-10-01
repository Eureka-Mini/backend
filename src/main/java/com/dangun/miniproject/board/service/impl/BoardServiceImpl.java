package com.dangun.miniproject.board.service.impl;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.board.dto.*;
import com.dangun.miniproject.board.exception.BoardNotFoundException;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.board.service.BoardService;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.comment.dto.GetCommentResponse;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.common.exception.InvalidInputException;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.exception.MemberNotFoundException;
import com.dangun.miniproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        final Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

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

        final Page<Board> boards = boardRepository.findAllWithMember(pageable);

        return boards.map(GetBoardResponse::from);
    }

    /**
     * 게시글 키워드 검색
     */
    @Override
    public Page<GetBoardResponse> getBoardList(final String keyword, final Pageable pageable) {

        final Page<Board> boards = boardRepository.searchBoardsByKeyword(keyword, pageable);

        return boards.map(GetBoardResponse::from);
    }

    /**
     * 작성 게시글 목록 조회
     */
    @Override
    public Page<GetBoardResponse> getMyBoardList(final Long memberId, final Pageable pageable) {

        final Page<Board> boards = boardRepository.findAllByMyBoard(memberId, pageable);

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
                .orElseThrow(MemberNotFoundException::new);
        CodeKey codeKey = new CodeKey(BoardStatus.판매중.getGroupId(), BoardStatus.판매중.getCodeId());

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .codeKey(codeKey)
                .price(request.getPrice())
                .build();

        Board savedBoard = boardRepository.save(board);

        return new WriteBoardResponse(savedBoard.getId());
    }

    /**
     * 게시글 수정
     */
    @Override
    @Transactional
    public UpdateBoardResponse updateBoard(Long boardId, UpdateBoardRequest request, Long memberId) {
        if (memberId == 0) {
            throw new InternalAuthenticationServiceException("Token Not Exist") {
            };
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        if (!board.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("Is not writer");
        }

        // 부분 업데이트 로직
        //null이면 그 필드는 업데이트하지 않고 기존 값을 유지
        BoardStatus updatedStatus = tryParseBoardStatus(request.getBoardStatus());

        board.updateDetails(
                request.getTitle() != null ? request.getTitle() : board.getTitle(),
                request.getContent() != null ? request.getContent() : board.getContent(),
                request.getPrice() != null ? request.getPrice() : board.getPrice(),
                request.getBoardStatus() != null
                        ? new CodeKey(updatedStatus.getGroupId(), updatedStatus.getCodeId()) : board.getCodeKey()
        );

        // 응답 생성
        return new UpdateBoardResponse(board.getContent());
    }


    /**
     * 게시글 삭제
     */
    @Override
    @Transactional
    public DeleteBoardResponse deleteBoard(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        if (!board.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("Is not writer");
        }

        boardRepository.delete(board);

        return DeleteBoardResponse.builder()
                .code("BOARD-S004")
                .message("Board Delete Success")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private BoardStatus tryParseBoardStatus(String status) {
        try {
            return BoardStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid Board Status");
        }
    }

}
