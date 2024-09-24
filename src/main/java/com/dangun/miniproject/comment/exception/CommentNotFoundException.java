package com.dangun.miniproject.comment.exception;

import com.dangun.miniproject.common.exception.NotFoundException;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException() {
        super("COMMENT-", "Comment not found");
    }
}
