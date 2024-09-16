package com.dangun.miniproject.board.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.dangun.miniproject.board.dto.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.auth.dto.UserDetailsDto;
import com.dangun.miniproject.fixture.BoardFixture;
import com.dangun.miniproject.board.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			given(member.getId()).willReturn(1L);

			final UserDetailsDto userDetails = new UserDetailsDto(member);

			final Board board1 = BoardFixture.instanceOf(member);
			final Board board2 = BoardFixture.instanceOf(member);

			final List<GetBoardResponse> boardList = List.of(
					GetBoardResponse.from(board1),
					GetBoardResponse.from(board2)
			);

			final Page<GetBoardResponse> response = new PageImpl<>(boardList, pageable, boardList.size());

			given(boardService.getMyBoardList(anyLong(), any(Pageable.class))).willReturn(response);

			// when -- 테스트하고자 하는 행동
			final ResultActions result = mockMvc.perform(
					get("/boards/my-board")
							.accept(APPLICATION_JSON)
							.contentType(APPLICATION_JSON)
							.with(authentication(UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities())))
							.with(csrf()));

			// then -- 예상되는 변화 및 결과
			result.andExpect(status().isOk());
		}
	}



	@Test
	@DisplayName("게시글 생성")
	void testWriteBoard_Success() throws Exception {
		// Given
		Long memberId = 1L;
		WriteBoardRequest request = new WriteBoardRequest("test", "test content");
		WriteBoardResponse response = WriteBoardResponse.builder()
				.code("BOARD-S001")
				.message("Board Write Success")
				.data(WriteBoardResponse.BoardData.builder().id(1L).build())
				.build();

		Member mockMember = Member.builder()
				.email("test@example.com")
				.nickname("testUser")
				.password("password")
				.build();
		ReflectionTestUtils.setField(mockMember, "id", memberId);
		UserDetailsDto userDetailsDto = new UserDetailsDto(mockMember);

		when(boardService.writeBoard(any(WriteBoardRequest.class), eq(memberId))).thenReturn(response);

		// When & Then
		mockMvc.perform(post("/boards")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf())
						.with(user(userDetailsDto)))
				.andExpect(status().isCreated())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@DisplayName("존재하지 않는 회원으로 게시글 생성 요청")
	void testCreateBoard_MemberNotFound() throws Exception {
		// Given
		WriteBoardRequest request = new WriteBoardRequest("Test Title", "Test Content");
		when(boardService.writeBoard(any(WriteBoardRequest.class), any(Long.class)))
				.thenThrow(new UsernameNotFoundException("Member not found"));

		// When & Then
		mockMvc.perform(post("/boards")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(user("nonexistentuser").roles("USER")))
				.andExpect(status().isForbidden())
				.andDo(print());
	}

	@Test
	@DisplayName("게시글 수정")
	void testUpdateBoard_Success() throws Exception {
		// Given
		Long boardId = 1L;
		Long memberId = 1L;
		UpdateBoardRequest request = new UpdateBoardRequest("test content");
		UpdateBoardResponse response = UpdateBoardResponse.builder()
				.code("BOARD-S002")
				.message("Board Update Success")
				.data(UpdateBoardResponse.Data.builder().content("test content").build())
				.build();

		Member mockMember = Member.builder()
				.email("test@example.com")
				.nickname("testUser")
				.password("password")
				.build();
		ReflectionTestUtils.setField(mockMember, "id", memberId);
		UserDetailsDto userDetailsDto = new UserDetailsDto(mockMember);

		when(boardService.updateBoard(eq(boardId), any(UpdateBoardRequest.class), eq(memberId)))
				.thenReturn(response);

		// When & Then
		mockMvc.perform(put("/boards/{boardId}", boardId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf())
						.with(user(userDetailsDto)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}



	@Test
	@DisplayName("존재하지 않는 회원이 수정 시도")
	void testUpdateBoard_MemberNotFound() throws Exception {
		// Given
		Long boardId = 1L;
		Long nonExistentMemberId = 99L;
		UpdateBoardRequest request = new UpdateBoardRequest("Updated Content");

		Member mockMember = Member.builder()
				.email("nonexistent@example.com")
				.nickname("nonexistentUser")
				.password("password")
				.build();
		ReflectionTestUtils.setField(mockMember, "id", nonExistentMemberId);
		UserDetailsDto userDetailsDto = new UserDetailsDto(mockMember);

		when(boardService.updateBoard(eq(boardId), any(UpdateBoardRequest.class), eq(nonExistentMemberId)))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

		// When & Then
		mockMvc.perform(put("/boards/{id}", boardId)
						.with(user(userDetailsDto))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
						.with(csrf()))
				.andExpect(status().isNotFound())
				.andDo(print());
	}

	@Test
	@DisplayName("토큰 없는 사용자 게시글 수정 시도")
	@WithAnonymousUser
	void testUpdateBoard_Unauthorized() throws Exception {
		// Given
		Long boardId = 1L;
		UpdateBoardRequest request = new UpdateBoardRequest("Updated Content");

		// When & Then
		MvcResult result = mockMvc.perform(put("/boards/{id}", boardId)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized())
				.andDo(print())
				.andReturn();

		String responseContent = result.getResponse().getContentAsString();
		if (!responseContent.isEmpty()) {
			DocumentContext context = JsonPath.parse(responseContent);
			assertThat(context.read("$.code", String.class)).isEqualTo("BOARD-F002");
			assertThat(context.read("$.message", String.class)).isEqualTo("Token Not Exist");
			assertThat(context.<Object>read("$.data")).isNull();
		}
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void testDeleteBoard_Success() throws Exception {
		// Given
		Long boardId = 1L;
		Long memberId = 1L;
		DeleteBoardResponse response = DeleteBoardResponse.builder()
				.code("BOARD-S003")
				.message("Board Delete Success")
				.build();

		Member mockMember = Member.builder()
				.email("test@example.com")
				.nickname("testUser")
				.password("password")
				.build();
		ReflectionTestUtils.setField(mockMember, "id", memberId);
		UserDetailsDto userDetailsDto = new UserDetailsDto(mockMember);

		when(boardService.deleteBoard(eq(boardId), eq(memberId))).thenReturn(response);

		// When & Then
		mockMvc.perform(delete("/boards/{boardId}", boardId)
						.with(csrf())
						.with(user(userDetailsDto)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	@DisplayName("존재하지 않은 작성자에 의해 게시글 삭제 시도")
	void testDeleteBoard_MemberNotFound() throws Exception {
		// Given
		Long boardId = 1L;
		Long nonExistentMemberId = 99L;

		Member mockMember = Member.builder()
				.email("nonexistent@example.com")
				.nickname("nonexistentUser")
				.password("password")
				.build();
		ReflectionTestUtils.setField(mockMember, "id", nonExistentMemberId);
		UserDetailsDto userDetailsDto = new UserDetailsDto(mockMember);

		when(boardService.deleteBoard(eq(boardId), any(Long.class)))
				.thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User Not Found"));

		// When & Then
		mockMvc.perform(delete("/boards/{id}", boardId)
						.with(user(userDetailsDto))
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(result -> assertThat(result.getResolvedException())
						.isInstanceOf(ResponseStatusException.class)
						.hasMessageContaining("User Not Found"))
				.andDo(print());

		verify(boardService).deleteBoard(eq(boardId), any(Long.class));
	}

}