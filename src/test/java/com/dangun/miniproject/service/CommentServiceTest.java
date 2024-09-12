package com.dangun.miniproject.service;

import com.dangun.miniproject.domain.Board;
import com.dangun.miniproject.domain.BoardStatus;
import com.dangun.miniproject.domain.Comment;
import com.dangun.miniproject.domain.Member;
import com.dangun.miniproject.dto.UpdateCommentRequest;
import com.dangun.miniproject.dto.UpdateCommentResponse;
import com.dangun.miniproject.dto.WriteCommentRequest;
import com.dangun.miniproject.dto.WriteCommentResponse;
import com.dangun.miniproject.repository.BoardRepository;
import com.dangun.miniproject.repository.CommentRepository;
import com.dangun.miniproject.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
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
            Board board = Board.builder()
                    .title("test")
                    .boardStatus(BoardStatus.판매완료)
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
            ).isInstanceOf(NoSuchElementException.class).hasMessage("Board Not Found.");
        }
    }

    @Nested
    class updateComment {

        @Test
        void 유효한_사용자가_본인의_댓글을_수정한다() {
            // given
            Long commentId = 1L;
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

            // when
            UpdateCommentResponse response = commentService.updateComment(commentId, request, member);

            // then
            assertThat(response.getContent()).isEqualTo(updatedContent);
        }
    }
}
