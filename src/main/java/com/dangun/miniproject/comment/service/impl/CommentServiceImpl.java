package com.dangun.miniproject.comment.service.impl;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.exception.BoardNotFoundException;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.comment.dto.UpdateCommentRequest;
import com.dangun.miniproject.comment.dto.UpdateCommentResponse;
import com.dangun.miniproject.comment.dto.WriteCommentRequest;
import com.dangun.miniproject.comment.dto.WriteCommentResponse;
import com.dangun.miniproject.comment.exception.CommentNotFoundException;
import com.dangun.miniproject.comment.repository.CommentRepository;
import com.dangun.miniproject.comment.service.CommentService;
import com.dangun.miniproject.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Override
    public WriteCommentResponse writeComment(Member member, Long boardId, WriteCommentRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        Comment comment = request.toEntity(member, board);
        comment = commentRepository.save(comment);

        return new WriteCommentResponse(comment.getContent());
    }

    @Override
    public UpdateCommentResponse updateComment(Long boardId, Long commentId, Member member, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new BoardNotFoundException();
        }

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("Is not writer");
        }

        comment.updateContent(request.getContent());

        return new UpdateCommentResponse(comment.getContent());
    }

    @Override
    public void deleteComment(Long boardId, Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new BoardNotFoundException();
        }

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("Is not writer");
        }

        commentRepository.delete(comment);
    }
}
