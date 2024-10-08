package com.dangun.miniproject.like.repository;

import com.dangun.miniproject.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
