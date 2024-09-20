package com.dangun.miniproject.comment.repository;

import com.dangun.miniproject.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
