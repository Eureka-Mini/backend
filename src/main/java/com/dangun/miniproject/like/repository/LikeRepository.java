package com.dangun.miniproject.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dangun.miniproject.like.domain.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
