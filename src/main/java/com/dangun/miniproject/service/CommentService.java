package com.dangun.miniproject.service;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UpdateCommentRequest;
import com.dangun.miniproject.dto.UpdateCommentResponse;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;

public interface CommentService {

    WriteCommentResponse writeComment(Member member, Long boardId, WriteCommentRequest comment);

    UpdateCommentResponse updateComment(Long commentId, Long boardId, UpdateCommentRequest request, Member member);
}