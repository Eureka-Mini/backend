package com.dangun.miniproject.comment.service;

import com.dangun.miniproject.board.domain.Board;
import com.dangun.miniproject.board.domain.BoardStatus;
import com.dangun.miniproject.board.exception.BoardNotFoundException;
import com.dangun.miniproject.board.repository.BoardRepository;
import com.dangun.miniproject.comment.domain.Comment;
import com.dangun.miniproject.comment.dto.UpdateCommentRequest;
import com.dangun.miniproject.comment.dto.UpdateCommentResponse;
import com.dangun.miniproject.comment.dto.WriteCommentRequest;
import com.dangun.miniproject.comment.dto.WriteCommentResponse;
import com.dangun.miniproject.comment.exception.CommentNotFoundException;
import com.dangun.miniproject.comment.repository.CommentRepository;
import com.dangun.miniproject.comment.service.impl.CommentServiceImpl;
import com.dangun.miniproject.common.code.CodeKey;
import com.dangun.miniproject.member.domain.Member;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Nested
    class writeComment {

        @Test
        void 유효한_사용자가_게시글에_댓글을_생성한다() {
            // given
            Member member = Member.builder()
                    .email("asdf@asdf.com")
                    .nickname("test")
                    .password("asdf")
                    .build();

            CodeKey codeKey = new CodeKey(BoardStatus.판매완료.getGroupId(), BoardStatus.판매완료.getCodeId());

            Board board = Board.builder()
                    .title("test")
                    .codeKey(codeKey)
                    .member(member)
                    .price(10000)
                    .content("팝니다")
                    .build();

            WriteCommentRequest request = mock(WriteCommentRequest.class);
            Comment comment = mock(Comment.class);

            when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
            when(comment.getContent()).thenReturn("댓글 테스트");
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(request.toEntity(member, board)).thenCallRealMethod();

            // when
            WriteCommentResponse response = commentService.writeComment(member, 1L, request);

            // then
            verify(commentRepository).save(any(Comment.class));
            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEqualTo(comment.getContent()).isEqualTo("댓글 테스트");
        }

        @Test
        void 존재하지_않는_게시글에_댓글_작성_시도() {
            // given
            Member member = mock(Member.class);
            WriteCommentRequest request = mock(WriteCommentRequest.class);

            when(boardRepository.findById(0L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> commentService.writeComment(member, 0L, request)
            ).isInstanceOf(BoardNotFoundException.class).hasMessage("Board not found");
        }
    }

    @Nested
    class updateComment {

        @Test
        void 유효한_사용자가_본인의_댓글을_수정한다() {
            // given
            Long commentId = 10L;
            Long boardId = 20L;
            String updatedContent = "수정된 댓글 내용";
            UpdateCommentRequest request = mock(UpdateCommentRequest.class);
            Member member = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .content("수정 전 댓글")
                    .board(board)
                    .member(member)
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(request.getContent()).thenReturn(updatedContent);
            when(board.getId()).thenReturn(boardId);

            // when
            UpdateCommentResponse response = commentService.updateComment(boardId, commentId, member, request);

            // then
            assertThat(response.getContent()).isEqualTo(updatedContent);
        }

        @Test
        void 유효하지_않은_게시글_ID가_주어진_경우() {
            // given
            Long commentId = 10L;
            Long invalidBoardId = 999L;
            UpdateCommentRequest request = mock(UpdateCommentRequest.class);
            Member member = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .content("수정 전 댓글")
                    .board(board)
                    .member(member)
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(board.getId()).thenReturn(20L);

            // when, then
            assertThatThrownBy(() -> commentService.updateComment(invalidBoardId, commentId, member, request))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage("Board not found");
        }

        @Test
        void 다른_사용자가_댓글을_수정하려고_할_때_권한_없음() {
            // given
            Long commentId = 10L;
            Long boardId = 20L;
            UpdateCommentRequest request = mock(UpdateCommentRequest.class);
            Member actualMember = mock(Member.class);
            Member anotherMember = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .content("수정 전 댓글")
                    .board(board)
                    .member(actualMember)
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(board.getId()).thenReturn(boardId);
            when(actualMember.getId()).thenReturn(1L);
            when(anotherMember.getId()).thenReturn(2L);

            // when, then
            assertThatThrownBy(() -> commentService.updateComment(boardId, commentId, anotherMember, request))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("Is not writer");
        }

        @Test
        void 존재하지_않는_댓글을_수정한다_400(){
                // given
                Long commentId = 10L;
                Long boardId = 20L;
                UpdateCommentRequest request = mock(UpdateCommentRequest.class);
                Member member = mock(Member.class);

                when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

                // when & then
            assertThatThrownBy(() -> commentService.updateComment(boardId, commentId, member, request))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessage("Comment not found");
            }
    }

    @Nested
    class deleteComment {

        @Test
        void 유효한_사용자가_본인의_댓글을_삭제한다() {
            // given
            Long commentId = 10L;
            Long boardId = 20L;
            Member member = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .board(board)
                    .member(member)
                    .content("댓글 내용")
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(board.getId()).thenReturn(boardId);
            when(member.getId()).thenReturn(1L);

            // when
            commentService.deleteComment(boardId, commentId, member);

            // then
            verify(commentRepository).delete(comment);
        }

        @Test
        void 유효하지_않은_게시글_ID가_주어진_경우() {
            // given
            Long commentId = 10L;
            Long invalidBoardId = 999L;
            Member member = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .board(board)
                    .member(member)
                    .content("댓글 내용")
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(board.getId()).thenReturn(20L);

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(invalidBoardId, commentId, member))
                    .isInstanceOf(BoardNotFoundException.class)
                    .hasMessage("Board not found");
        }

        @Test
        void 다른_사용자가_댓글을_삭제하려고_할_때_권한_없음() {
            // given
            Long commentId = 10L;
            Long boardId = 20L;
            Member actualMember = mock(Member.class);
            Member anotherMember = mock(Member.class);
            Board board = mock(Board.class);
            Comment comment = Comment.builder()
                    .board(board)
                    .member(actualMember)
                    .content("댓글 내용")
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(board.getId()).thenReturn(boardId);
            when(actualMember.getId()).thenReturn(1L);
            when(anotherMember.getId()).thenReturn(2L);

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(boardId, commentId, anotherMember))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("Is not writer");
        }
    }
}
