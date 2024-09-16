package com.dangun.miniproject.comment.service;

import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.comment.dto.UpdateCommentRequest;
import com.dangun.miniproject.comment.dto.UpdateCommentResponse;
import com.dangun.miniproject.comment.dto.WriteCommentRequest;
import com.dangun.miniproject.comment.dto.WriteCommentResponse;

public interface CommentService {

    WriteCommentResponse writeComment(Member member, Long boardId, WriteCommentRequest comment);

    UpdateCommentResponse updateComment(Long boardId, Long commentId, Member member, UpdateCommentRequest request);

    void deleteComment(Long boardId, Long commentId, Member member);
}