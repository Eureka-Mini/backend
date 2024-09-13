package com.dangun.miniproject.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.dangun.miniproject.domain.BoardStatus;
import com.dangun.miniproject.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.fixture.BoardFixture;
import com.dangun.miniproject.service.BoardService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(controllers = BoardController.class)
@WithMockUser
class BoardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BoardService boardService;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("게시글 상세 조회")
	class getBoardDetail {

		@Test
		@DisplayName("[성공] 게시글 상세 정보가 정상적으로 조회된다.")
		void getBoardDetail_success() throws Exception {
			// given -- 테스트의 상태 설정
			final Member member = mock(Member.class);
			final Board board = BoardFixture.instanceOf(member);

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

			final Member member = mock(Member.class);
			final Board board1 = BoardFixture.instanceOf(member);
			final Board board2 = BoardFixture.instanceOf(member);

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

			final Member member = mock(Member.class);
			final Board board1 = BoardFixture.instanceOf(member);
			final Board board2 = BoardFixture.instanceOf(member);

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

			final Member member = mock(Member.class);
			final Board board1 = BoardFixture.instanceOf(member);
			final Board board2 = BoardFixture.instanceOf(member);

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



	@Test
	@DisplayName("게시글 생성")
	@WithMockUser(username = "testUser", roles = "USER")
	public void testCreateBoard() throws Exception {
		// Given
		CreateBoardRequest request = new CreateBoardRequest("Title", "Content", 100, BoardStatus.판매중, 1L);
		BoardResponse response = new BoardResponse(1L, "Title", "Content", 100, BoardStatus.판매중, 1L);
		when(boardService.createBoard(any(CreateBoardRequest.class))).thenReturn(response);

		// When
		mockMvc.perform(MockMvcRequestBuilders.post("/boards")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf()))
				//Then
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)));

		verify(boardService).createBoard(any(CreateBoardRequest.class));
	}



	@Test
	@DisplayName("존재하지 않는 작성자에 의해 게시글 생성")
	void testCreateBoard_MemberNotFound() throws Exception {
		// Given
		Mockito.when(boardService.createBoard(any(CreateBoardRequest.class)))
				.thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User Not Found"));
		// When
		mockMvc.perform(post("/boards")
						.with(csrf())
						.with(SecurityMockMvcRequestPostProcessors.user("testUser"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Test Title\",\"content\":\"Test Content\",\"price\":1000,\"memberId\":99}")
				)
				// Then
				.andDo(print())
				.andExpect(status().isForbidden());
	}



	@Test
	@DisplayName("게시글 수정")
	@WithMockUser(username = "testUser", roles = "USER")
	public void testUpdateBoard() throws Exception {
		// Given
		Long boardId = 1L;
		UpdateBoardRequest request = new UpdateBoardRequest("Updated Title", "Updated Content", 200, BoardStatus.판매완료);
		BoardResponse response = new BoardResponse(boardId, "Updated Title", "Updated Content", 200, BoardStatus.판매완료, 1L);
		when(boardService.updateBoard(anyLong(), any(UpdateBoardRequest.class))).thenReturn(response);

		// When
		mockMvc.perform(MockMvcRequestBuilders.put("/boards/{id}", boardId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf()))
				// Then
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)));

		verify(boardService).updateBoard(eq(boardId), any(UpdateBoardRequest.class));
	}



	@Test
	@DisplayName("존재하지 않는 회원이 수정")
	void testUpdateBoard_MemberNotFound() throws Exception {
		// Given
		Mockito.when(boardService.updateBoard(eq(1L), any(UpdateBoardRequest.class)))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

		// When
		mockMvc.perform(put("/boards/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Updated Title\",\"content\":\"Updated Content\",\"price\":2000}")
						.with(csrf())
						.with(SecurityMockMvcRequestPostProcessors.user("testUser"))
				)
				// Then
				.andDo(print())
				.andExpect(status().isNotFound());
	}



	@Test
	@DisplayName("존재하지 않은 게시글에 대한 수정")
	void testUpdateBoard_BoardNotFound() throws Exception {
		// Given
		Mockito.when(boardService.updateBoard(eq(99L), any(UpdateBoardRequest.class)))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Board Not Found"));
		// When
		mockMvc.perform(put("/boards/99")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"Updated Title\",\"content\":\"Updated Content\",\"price\":2000}")
						.with(csrf())
						.with(SecurityMockMvcRequestPostProcessors.user("testUser"))
				)
				// Then
				.andDo(print())
				.andExpect(status().isNotFound());
	}




	@Test
	@DisplayName("게시글 삭제")
	@WithMockUser(username = "testUser", roles = "USER")
	public void testDeleteBoard() throws Exception {
		// Given
		Long boardId = 1L;

		// When
		mockMvc.perform(MockMvcRequestBuilders.delete("/boards/{id}", boardId)
						.with(csrf()))
				// Then
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(boardService).deleteBoard(boardId);
	}




	@Test
	@DisplayName("존재하지 않은 작성자에 의해 게시글 삭제")
	void testDeleteBoard_MemberNotFound() throws Exception {
		// Given
		Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User Not Found"))
				.when(boardService).deleteBoard(1L);
		// When
		mockMvc.perform(delete("/boards/1")
						.with(csrf())
						.with(SecurityMockMvcRequestPostProcessors.user("testUser"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"code\":\"MEMBER-F001\",\"message\":\"User Not Found\"}")
				)
				// Then
				.andDo(print())
				.andExpect(status().isForbidden());
	}

}