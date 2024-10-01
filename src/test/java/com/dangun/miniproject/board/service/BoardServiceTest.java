package com.dangun.miniproject.board.service;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.board.dto.*;
import com.dangun.miniproject.board.exception.BoardNotFoundException;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.board.service.impl.BoardServiceImpl;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.fixture.BoardFixture;
import com.dangun.miniproject.fixture.CommentFixture;
import com.dangun.miniproject.member.domain.Address;
import com.dangun.miniproject.member.domain.Member;
import com.dangun.miniproject.member.repository.MemberRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.by;


@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardServiceImpl boardServiceImpl;

    @InjectMocks
    private BoardServiceImpl boardService;

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
            final Address address = mock(Address.class);
            final Board board = BoardFixture.instanceOf(member);

            board.getComments().addAll(new ArrayList<>());

            given(boardRepository.findById(any())).willReturn(Optional.of(board));
            given(member.getAddress()).willReturn(address);
            given(address.getStreet()).willReturn("test Street");

            final GetBoardDetailResponse response = GetBoardDetailResponse.from(board);

            given(boardRepository.findBoardById(any())).willReturn(response);

            // when -- 테스트하고자 하는 행동
            final GetBoardDetailResponse result = boardServiceImpl.getBoardDetail(1L);

            // then -- 예상되는 변화 및 결과
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isNotNull();
                softAssertions.assertThat(result.getId()).isEqualTo(response.getId());
                softAssertions.assertThat(result.getTitle()).isEqualTo(response.getTitle());
                softAssertions.assertThat(result.getWriter()).isEqualTo(response.getWriter());
            });
        }

        @Test
        @DisplayName("[성공] 게시글 작성자 정보와 함께 댓글 목록이 최신순으로 조회된다.")
        void getBoardDetail_commentsAndWriterInfo_success() {
            // given -- 테스트의 상태 설정
            final Member member = mock(Member.class);
            final Board board = mock(Board.class);
            final Address address = mock(Address.class);
            final Comment comment1 = CommentFixture.instanceOf(member, board);
            final Comment comment2 = CommentFixture.instanceOf(member, board);

            when(board.getComments()).thenReturn(List.of(comment1, comment2));
            when(member.getId()).thenReturn(1L);
            when(board.getMember()).thenReturn(member);

            given(boardRepository.findById(any())).willReturn(Optional.of(board));
            when(member.getAddress()).thenReturn(address);
            when(address.getStreet()).thenReturn("test Street");

            final GetBoardDetailResponse response = GetBoardDetailResponse.from(board);

            given(boardRepository.findBoardById(any())).willReturn(response);

            // when -- 테스트하고자 하는 행동
            final GetBoardDetailResponse result = boardServiceImpl.getBoardDetail(1L);

            // then -- 예상되는 변화 및 결과
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isNotNull();
                softAssertions.assertThat(result.getId()).isEqualTo(response.getId());
                softAssertions.assertThat(result.getComments().size()).isEqualTo(2);

                // comment
                softAssertions.assertThat(result.getId()).isNotNull();
                softAssertions.assertThat(result.getComments().get(0).getId()).isEqualTo(comment1.getId());
                softAssertions.assertThat(result.getComments().get(0).getContent()).isEqualTo(comment1.getContent());
                softAssertions.assertThat(result.getComments().get(0).getWriterId()).isEqualTo(1L);
                softAssertions.assertThat(result.getComments().get(0).getWriter()).isEqualTo(comment1.getMember().getNickname());
                softAssertions.assertThat(result.getComments().get(0).isBoardWriter()).isTrue();
                softAssertions.assertThat(result.getComments().get(0).getCreatedAt()).isEqualTo(comment1.getCreatedAt());
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
                // board
                softAssertions.assertThat(result.getContent().get(0).getTitle()).isEqualTo(response.getContent().get(0).getTitle());
//                softAssertions.assertThat(result.getContent().get(0).getBoardStatus()).isEqualTo(response.getContent().get(0).getBoardStatus());

                // page
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


    @Test
    @DisplayName("게시글 생성")
    public void testWriteBoard() {
        // Given
        Long memberId = 1L;
        Long boardId = 1L;

        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);

        WriteBoardRequest request = new WriteBoardRequest("제목", "내용", 1000);

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .build();
        ReflectionTestUtils.setField(board, "id", boardId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        // When
        WriteBoardResponse response = boardService.writeBoard(request, memberId);

        // Then
        assertNotNull(response);
        assertEquals("BOARD-S001", response.getCode());
        assertEquals("Board Write Success", response.getMessage());
        assertEquals(boardId, response.getData().getId());
        assertNotNull(response.getTimestamp());

        verify(memberRepository).findById(memberId);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("존재하지 않는 작성자에 의해 게시글 생성")
    public void testWriteBoardWhenMemberNotFound() {
        // Given
        Long memberId = 99L;
        WriteBoardRequest request = new WriteBoardRequest("제목", "내용", 1000);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> boardService.writeBoard(request, memberId), "Member not found");
    }


    @Test
    @DisplayName("게시글 수정")
    public void testUpdateBoard() {
        // Given
        Long boardId = 1L;
        Long memberId = 1L;

        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);

        Board existingBoard = Board.builder()
                .title("기존 제목")
                .content("기존 내용")
                .price(500)
                .codeKey(new CodeKey(BoardStatus.판매중.getCodeId(), BoardStatus.판매중.getGroupId()))
                .member(member)
                .build();
        ReflectionTestUtils.setField(existingBoard, "id", boardId);

        UpdateBoardRequest request = new UpdateBoardRequest("새 제목", "새 내용", 1000, "판매완료");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));

        // When
        Map<String, String> response = boardService.updateBoard(boardId, request, memberId);

        // Then
        assertNotNull(response);
        assertEquals("새 내용", response.get("content"));
        assertEquals("새 제목", existingBoard.getTitle());
        assertEquals("새 내용", existingBoard.getContent());
        assertEquals(1000, existingBoard.getPrice());
        assertEquals(BoardStatus.판매완료.getCodeId(), existingBoard.getCodeKey().getCode());
        assertEquals(BoardStatus.판매완료.getGroupId(), existingBoard.getCodeKey().getGroupCode());

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("존재하지 않는 회원이 수정")
    public void testUpdateBoardWithNonExistentMember() {
        // Given
        Long boardId = 1L;
        Long nonExistentMemberId = 999L;
        UpdateBoardRequest request = new UpdateBoardRequest("새 제목", "새 내용", 1000, "판매중");

        Member existingMember = new Member();
        ReflectionTestUtils.setField(existingMember, "id", 1L);

        Board existingBoard = Board.builder()
                .content("기존 내용")
                .member(existingMember)
                .price(1000)
                .codeKey(new CodeKey(BoardStatus.판매중.getCodeId(), BoardStatus.판매중.getGroupId()))
                .title("기존 제목")
                .build();
        ReflectionTestUtils.setField(existingBoard, "id", boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                boardService.updateBoard(boardId, request, nonExistentMemberId)
        );
        assertEquals("Is not writer", thrown.getMessage());
    }


    @Test
    @DisplayName("토큰이 없을 때 수정 요청")
    public void testUpdateBoardWithNoToken() {
        // Given
        Long boardId = 1L;
        Long memberId = 0L; // 인증되지 않은 사용자 (토큰이 없음)
        UpdateBoardRequest request = new UpdateBoardRequest("새 제목", "새 내용", 1000, "판매중");

        // When
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            boardService.updateBoard(boardId, request, memberId);
        });
        // Then
        assertEquals("Token Not Exist", thrown.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 대한 수정")
    public void testUpdateNonExistentBoard() {
        // Given
        Long nonExistentBoardId = 999L;
        UpdateBoardRequest request = new UpdateBoardRequest("새 제목", "새 내용", 1000, "판매중");

        when(boardRepository.findById(nonExistentBoardId)).thenReturn(Optional.empty());

        // When
        RuntimeException thrown = assertThrows(BoardNotFoundException.class, () -> {
            boardService.updateBoard(nonExistentBoardId, request, 1L);
        });
        // Then
        assertEquals("Board not found", thrown.getMessage());
    }


    @Test
    public void testUpdateBoard_withNullValues() {
        // given
        Long boardId = 1L;
        Long memberId = 1L;
        UpdateBoardRequest request = new UpdateBoardRequest(
                null,  // Title을 null로 설정
                null,  // Content을 null로 설정
                null,  // Price를 null로 설정
                "판매완료"  // BoardStatus는 null이 아닌 값으로 설정
        );

        Member member = mock(Member.class);

        Board existingBoard = spy(Board.builder()
                .title("Existing Title")
                .content("Existing Content")
                .price(1000)
                .codeKey(new CodeKey(BoardStatus.판매중.getCodeId(), BoardStatus.판매중.getGroupId()))
                .member(member)
                .build());

        when(member.getId()).thenReturn(memberId);
        when(boardRepository.findById(eq(boardId))).thenReturn(Optional.of(existingBoard));

        // when
        Map<String, String> response = boardService.updateBoard(boardId, request, memberId);

        // then
        assertNotNull(response);

        // 변경되지 않아야 할 필드들 확인
        assertEquals("Existing Title", existingBoard.getTitle());
        assertEquals("Existing Content", existingBoard.getContent());
        assertEquals(1000, existingBoard.getPrice());

        // BoardStatus는 변경되어야 함 (request에서 null이 아닌 값으로 제공됨)
        assertEquals(BoardStatus.판매완료.getCodeId(), existingBoard.getCodeKey().getCode());
        assertEquals(BoardStatus.판매완료.getGroupId(), existingBoard.getCodeKey().getGroupCode());

        // boardRepository의 findById 메소드가 호출되었는지 확인
        verify(boardRepository, times(1)).findById(eq(boardId));
    }




    @Test
    @DisplayName("게시글 삭제 성공")
    public void testDeleteBoard() {
        // Given
        Long boardId = 1L;
        Long memberId = 1L;

        Member member = Member.builder()
                .email("test@example.com")
                .nickname("테스트닉네임")
                .password("password")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .price(1000)
                .codeKey(mock(CodeKey.class))
                .member(member)
                .build();
        ReflectionTestUtils.setField(board, "id", boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // When
        DeleteBoardResponse response = boardService.deleteBoard(boardId, memberId);

        // Then
        verify(boardRepository).findById(boardId);
        verify(boardRepository).delete(board);

        assertNotNull(response);
        assertEquals("BOARD-S004", response.getCode());
        assertEquals("Board Delete Success", response.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원이 게시글 삭제 시도")
    public void testDeleteBoardByNonExistentMember() {
        // Given
        Long boardId = 999L;
        Long nonExistentMemberId = 999L;

        Member existingMember = new Member();
        ReflectionTestUtils.setField(existingMember, "id", 1L);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .price(1000)
                .codeKey(mock(CodeKey.class))
                .member(existingMember)
                .build();
        ReflectionTestUtils.setField(board, "id", boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        // When & Then
        assertThrows(RuntimeException.class, () ->
                boardService.deleteBoard(boardId, nonExistentMemberId)
        );

        verify(boardRepository).findById(boardId);
        verify(boardRepository, never()).delete(any(Board.class));
    }
}