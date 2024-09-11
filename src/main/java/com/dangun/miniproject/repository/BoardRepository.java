package com.dangun.miniproject.repository;

import com.dangun.miniproject.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository  extends JpaRepository<Board, Long> {
    void deleteByMemberId(Long memberId);
}
