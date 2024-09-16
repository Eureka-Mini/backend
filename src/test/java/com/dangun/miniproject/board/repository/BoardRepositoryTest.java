package com.dangun.miniproject.board.repository;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.board.dto.GetBoardDetailResponse;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BoardRepositoryTest {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("[성공] 게시글 작성자 정보와 댓글이 최신순으로 상세 조회된다.")
	void getBoardDetail_withMemberAndComments_success() {
	    // given -- 테스트의 상태 설정
		final Board board = em.find(Board.class, 1L);

	    // when -- 테스트하고자 하는 행동
		final GetBoardDetailResponse result = boardRepository.findBoardById(1L);

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result.getTitle()).isEqualTo(board.getTitle());
			softAssertions.assertThat(result.getComments()).isSortedAccordingTo(
				(c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
		});
	}

	@Test
	@DisplayName("[성공] 작성자 정보가 포함된 게시글이 정상적으로 조회된다.")
	void getBoardList_withMember_success() {
	    // given -- 테스트의 상태 설정
		final Pageable pageable = PageRequest.of(0, 10);

	    // when -- 테스트하고자 하는 행동
		final Page<Board> result = boardRepository.findAllWithMember(pageable);

		// then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result).isNotNull();
			softAssertions.assertThat(result.getContent().get(0).getMember()).isNotNull();
		});
	}

	@Test
	@DisplayName("[성공] 키워드로 검색시 정상적으로 해당 게시글이 조회된다.")
	void getBoardList_searchByKeyword_success() {
	    // given -- 테스트의 상태 설정
		final String keyword = "keyword";
		final Pageable pageable = PageRequest.of(0, 10);

		// when -- 테스트하고자 하는 행동
		final Page<Board> result = boardRepository.searchBoardsByKeyword(keyword, pageable);

	    // then -- 예상되는 변화 및 결과
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("[성공] 자신이 작성한 게시글 목록이 정상적으로 조회된다.")
	void getMyBoardList_success() {
	    // given -- 테스트의 상태 설정
		final Member member = em.find(Member.class, 1L);
		final Pageable pageable = PageRequest.of(0, member.getBoards().size());

	    // when -- 테스트하고자 하는 행동
		final Page<Board> result = boardRepository.findAllByMyBoard(1L, pageable);

	    // then -- 예상되는 변화 및 결과
		assertSoftly(softAssertions -> {
			softAssertions.assertThat(result).isNotNull();
			softAssertions.assertThat(result.getContent()).isNotEmpty();
			softAssertions.assertThat(result.getSize()).isEqualTo(member.getBoards().size());
		});
	}
}
