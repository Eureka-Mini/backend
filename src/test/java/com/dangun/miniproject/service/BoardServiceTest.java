package com.dangun.miniproject.service;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.data.domain.Sort.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.GetBoardDetailResponse;
import com.dangun.miniproject.dto.GetBoardResponse;
import com.dangun.miniproject.dto.GetCommentResponse;
import com.dangun.miniproject.fixture.BoardFixture;
import com.dangun.miniproject.fixture.CommentFixture;
import com.dangun.miniproject.repository.BoardRepository;
import com.dangun.miniproject.repository.MemberRepository;
import com.dangun.miniproject.service.impl.BoardServiceImpl;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

	@InjectMocks
	private BoardServiceImpl boardServiceImpl;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private MemberRepository memberRepository;

	@Nested
	@DisplayName("게시글 상세 조회")
	class GetBoardDetail {

		@Test
		@DisplayName("[성공] 게시글 ID로 상세 정보가 정상적으로 조회된다.")
		void getBoardDetail_success() {
		    // given -- 테스트의 상태 설정
			final Member member = mock(Member.class);
			final Board board = BoardFixture.instanceOf(member);

			board.getComments().addAll(new ArrayList<>());

			given(boardRepository.findById(any())).willReturn(Optional.of(board));

			final GetBoardDetailResponse response = GetBoardDetailResponse.from(board);

		    // when -- 테스트하고자 하는 행동
			final GetBoardDetailResponse result = boardServiceImpl.getBoardDetail(1L);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result).isNotNull();
				softAssertions.assertThat(result.getId()).isEqualTo(response.getId());
			});
		}

		@Test
		@DisplayName("[성공] 게시글의 댓글 목록과 댓글 작성자 정보가 정상적으로 조회된다.")
		void getBoardDetail_commentsAndWriterInfo_success() {
		    // given -- 테스트의 상태 설정
			final Member member = mock(Member.class);
			final Board board = mock(Board.class);
			final Comment comment = CommentFixture.instanceOf(member, board);

			when(board.getComments()).thenReturn(List.of(comment));
			when(member.getId()).thenReturn(1L);
			when(board.getMember()).thenReturn(member);

			given(boardRepository.findById(any())).willReturn(Optional.of(board));

			final boolean isBoardWriter = board.getMember().getId().equals(comment.getMember().getId());
			final GetCommentResponse commentResponse = GetCommentResponse.from(comment, isBoardWriter);
			final GetBoardDetailResponse response = GetBoardDetailResponse.from(board);

			response.getComments().add(commentResponse);

		    // when -- 테스트하고자 하는 행동
			final GetBoardDetailResponse result = boardServiceImpl.getBoardDetail(1L);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result).isNotNull();
				softAssertions.assertThat(result.getId()).isEqualTo(response.getId());
				softAssertions.assertThat(result.getComments().size()).isEqualTo(response.getComments().size());
			});
		}
	}

	@Nested
	@DisplayName("게시글 목록 조회")
	class GetBoardList {

		@Test
		@DisplayName("[성공] 페이지당 게시글 목록이 10개씩 정상적으로 조회된다.")
		void getBoardList_paging_success() {
		    // given -- 테스트의 상태 설정
			final Member member = mock(Member.class);
			final PageRequest pageRequest = PageRequest.of(0, 10);

			final List<Board> boardList = Arrays.asList(
				BoardFixture.instanceOf(member),
				BoardFixture.instanceOf(member)
			);

			final PageImpl<Board> response = new PageImpl<>(boardList);

			given(boardRepository.findAllWithMember(any())).willReturn(response);

		    // when -- 테스트하고자 하는 행동
			final Page<GetBoardResponse> result = boardServiceImpl.getBoardList(pageRequest);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result.getTotalPages()).isEqualTo(response.getTotalPages());
				softAssertions.assertThat(result.getTotalElements()).isEqualTo(response.getTotalElements());
				softAssertions.assertThat(result.getContent().size()).isEqualTo(response.getSize());
			});
		}

		@Test
		@DisplayName("[성공] 키워드로 게시글 목록이 정상적으로 검색된다.")
		void getBoardList_searchByKeyword_success() {
			// given -- 테스트의 상태 설정
			final Member member = mock(Member.class);
			final PageRequest pageRequest = PageRequest.of(0, 10, by(Direction.DESC, "createdAt"));

			final List<Board> boardList = List.of(
				BoardFixture.instanceOf(member, "키워드 Title", "content"),
				BoardFixture.instanceOf(member, "일반 Title", "content")
			);

			final List<Board> searchBoardList = boardList.stream()
				.filter(board -> board.getTitle().contains("키워드"))
				.toList();

			final PageImpl<Board> response = new PageImpl<>(searchBoardList);

			given(boardRepository.searchBoardsByKeyword(eq("키워드"), any())).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final Page<GetBoardResponse> result = boardServiceImpl.getBoardList("키워드", pageRequest);

			// then -- 예상되는 변화 및 결과
			assertSoftly(softAssertions -> {
				softAssertions.assertThat(result.getTotalPages()).isEqualTo(1); // 결과가 하나의 페이지에 있어야 함
				softAssertions.assertThat(result.getTotalElements()).isEqualTo(1); // 키워드와 관련 있는 게시글 1개
				softAssertions.assertThat(result.getContent().size()).isEqualTo(1); // 실제 반환된 게시글의 개수 확인
			});
		}

		@Nested
		@DisplayName("작성 게시글 목록 조회")
		class GetMyBoardList {

			@Test
			@DisplayName("[성공] 자신이 작성한 게시글 목록이 정상적으로 조회된다.")
			void getMyBoardList_success() {
				// given -- 테스트의 상태 설정
				final Member member = mock(Member.class);
				final PageRequest pageRequest = PageRequest.of(0, 10);

				final List<Board> boardList = Arrays.asList(
					BoardFixture.instanceOf(member),
					BoardFixture.instanceOf(member)
				);

				final PageImpl<Board> response = new PageImpl<>(boardList);

				given(boardRepository.findAllByMyBoard(any(), any())).willReturn(response);

				// when -- 테스트하고자 하는 행동
				final Page<GetBoardResponse> result = boardServiceImpl.getMyBoardList(1L, pageRequest);

				// then -- 예상되는 변화 및 결과
				assertSoftly(softAssertions -> {
					softAssertions.assertThat(result.getTotalPages()).isEqualTo(response.getTotalPages());
					softAssertions.assertThat(result.getTotalElements()).isEqualTo(response.getTotalElements());
					softAssertions.assertThat(result.getContent().size()).isEqualTo(response.getSize());
				});
			}
		}
	}
}