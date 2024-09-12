package com.dangun.miniproject.service;

import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;

public interface CommentService {

    WriteCommentResponse writeComment(Member member, Long boardId, WriteCommentRequest comment);

    void updateComment(Long commentId, String updatedContent, Member member);
}