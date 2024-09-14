package com.dangun.miniproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.dto.GetBoardDetailResponse;

public interface BoardRepository extends JpaRepository<Board, Long> {

	void deleteByMemberId(Long memberId);

	// 게시글 상세 조회
	@Query(value = """
			SELECT b
			 FROM Board b
			 JOIN FETCH b.member m
			 LEFT JOIN FETCH b.comments c
			WHERE b.id = :boardId
			ORDER BY c.createdAt DESC
	""")
	GetBoardDetailResponse findBoardById(final Long boardId);


	// 게시글 목록 조회
	@Query(value = """
			SELECT b
			 FROM Board b
			 JOIN FETCH b.member
			ORDER BY b.createdAt DESC
	""")
	Page<Board> findAllWithMember(final Pageable pageable);

	// 게시글 키워드 검색
	@Query(value = """
			SELECT b
			  FROM Board b
			  JOIN FETCH b.member
			 WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
				OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
			 ORDER BY b.createdAt DESC
	""")
	Page<Board> searchBoardsByKeyword(@Param("keyword") final String keyword, final Pageable pageable);

	// 작성 게시글 목록 조회
	@Query(value = """
			SELECT b
			  FROM Board b
			  JOIN FETCH b.member m
			 WHERE m.id = :memberId
			 ORDER BY b.createdAt DESC
	""")
	Page<Board> findAllByMyBoard(final Long memberId, final Pageable pageable);

}
