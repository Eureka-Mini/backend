package com.dangun.miniproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dangun.miniproject.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
