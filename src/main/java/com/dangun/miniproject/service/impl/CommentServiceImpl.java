package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UpdateCommentRequest;
import com.dangun.miniproject.dto.UpdateCommentResponse;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;
import com.dangun.miniproject.repository.BoardRepository;
import com.dangun.miniproject.repository.CommentRepository;
import com.dangun.miniproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Override
    public WriteCommentResponse writeComment(Member member, Long boardId, WriteCommentRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NoSuchElementException("Board Not Found.")
        );

        Comment comment = request.toEntity(member, board);
        comment = commentRepository.save(comment);

        return new WriteCommentResponse(comment.getContent());
    }

    @Override
    public UpdateCommentResponse updateComment(Long boardId, Long commentId, Member member, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new NoSuchElementException("Board Not Found");
        }

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("You are not the owner of this comment");
        }

        comment.updateContent(request.getContent());

        return new UpdateCommentResponse(comment.getContent());
    }

    @Override
    public void deleteComment(Long boardId, Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        if (!comment.getBoard().getId().equals(boardId)) {
            throw new NoSuchElementException("Board Not Found");
        }

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("You are not the owner of this comment");
        }

        commentRepository.delete(comment);
    }
}
