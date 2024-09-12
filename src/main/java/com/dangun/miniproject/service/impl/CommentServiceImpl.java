package com.dangun.miniproject.service.impl;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;
import com.dangun.miniproject.repository.BoardRepository;
import com.dangun.miniproject.repository.CommentRepository;
import com.dangun.miniproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
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
}
