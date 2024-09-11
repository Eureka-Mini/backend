package com.dangun.miniproject.repository;

import com.dangun.miniproject.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByMemberId(Long memberId);
}
