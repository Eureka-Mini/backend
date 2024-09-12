package com.dangun.miniproject.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.dto.GetBoardDetailResponse;
import com.dangun.miniproject.dto.GetBoardResponse;
import com.dangun.miniproject.fixture.BoardFixture;
import com.dangun.miniproject.service.BoardService;

@WebMvcTest(controllers = BoardController.class)
@WithMockUser
class BoardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BoardService boardService;

	@Nested
	@DisplayName("게시글 상세 조회")
	class getBoardDetail {

		@Test
		@DisplayName("[성공] 게시글 상세 정보가 정상적으로 조회된다.")
		void getBoardDetail_success() throws Exception {
			// given -- 테스트의 상태 설정
			final Board board = BoardFixture.instanceOf();
			final GetBoardDetailResponse response = GetBoardDetailResponse.from(board);

			given(boardService.getBoardDetail(any())).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final ResultActions result = mockMvc.perform(
				get("/boards/{boardId}", 1L)
					.accept(APPLICATION_JSON)
					.contentType(APPLICATION_JSON));

			// then -- 예상되는 변화 및 결과
			result.andExpect(status().isOk());
		}

	}

	@Nested
	@DisplayName("게시글 목록 조회")
	class GetBoardList {

		@Test
		@DisplayName("[성공] 키워드가 없을 경우, 게시글 전체 목록이 조회된다.")
		void getBoardList_no_keyword_success() throws Exception {
			// given -- 테스트의 상태 설정
			final Pageable pageable = PageRequest.of(0, 10);

			final Board board1 = BoardFixture.instanceOf();
			final Board board2 = BoardFixture.instanceOf();

			final List<GetBoardResponse> boardList = List.of(
				GetBoardResponse.from(board1),
				GetBoardResponse.from(board2)
			);

			final Page<GetBoardResponse> response = new PageImpl<>(boardList, pageable, boardList.size());

			given(boardService.getBoardList(any())).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final ResultActions result = mockMvc.perform(
				get("/boards")
					.accept(APPLICATION_JSON)
					.contentType(APPLICATION_JSON));

			// then -- 예상되는 변화 및 결과
			result.andExpect(status().isOk());
		}

		@Test
		@DisplayName("[성공] 키워드가 있을 경우, 키워드가 포함된 게시글 목록이 조회된다.")
		void getBoardList_has_keyword_success() throws Exception {
			// given -- 테스트의 상태 설정
			final Pageable pageable = PageRequest.of(0, 10);

			final Board board1 = BoardFixture.instanceOf();
			final Board board2 = BoardFixture.instanceOf();

			final List<GetBoardResponse> boardList = List.of(
				GetBoardResponse.from(board1),
				GetBoardResponse.from(board2)
			);

			final Page<GetBoardResponse> response = new PageImpl<>(boardList, pageable, boardList.size());

			given(boardService.getBoardList(any(), any())).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final ResultActions result = mockMvc.perform(
				get("/boards")
					.param("keyword", "Test")
					.accept(APPLICATION_JSON)
					.contentType(APPLICATION_JSON));

			// then -- 예상되는 변화 및 결과
			result.andExpect(status().isOk());
		}
	}

	@Nested
	@DisplayName("작성 게시글 목록 조회")
	class GetMyBoardList {

		@Test
		@DisplayName("[성공] 자신이 작성한 게시글 목록이 조회된다.")
		void getMyBoardList_success() throws Exception {
			// given -- 테스트의 상태 설정
			final Pageable pageable = PageRequest.of(0, 10);

			final Board board1 = BoardFixture.instanceOf();
			final Board board2 = BoardFixture.instanceOf();

			final List<GetBoardResponse> boardList = List.of(
				GetBoardResponse.from(board1),
				GetBoardResponse.from(board2)
			);

			final Page<GetBoardResponse> response = new PageImpl<>(boardList, pageable, boardList.size());

			given(boardService.getMyBoardList(any(), any())).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final ResultActions result = mockMvc.perform(
				get("/boards/my-board")
					.param("memberId", "1")
					.accept(APPLICATION_JSON)
					.contentType(APPLICATION_JSON));

			// then -- 예상되는 변화 및 결과
			result.andExpect(status().isOk());
		}
	}

}