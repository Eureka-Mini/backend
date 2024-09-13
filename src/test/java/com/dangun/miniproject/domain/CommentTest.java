package com.dangun.miniproject.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

public class CommentTest {

    @Test
    void Comment_객체를_생성한다() {
        // given
        Member member = Member.builder()
                .email("email")
                .build();
        Board board = Board.builder()
                .title("제목")
                .build();
        String content = "테스트 댓글";

        // when
        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .content(content)
                .build();

        // then
        assertSoftly(softly -> {
            softly.assertThat(comment.getId()).isNull();
            softly.assertThat(comment.getBoard()).isEqualTo(board);
            softly.assertThat(comment.getMember()).isEqualTo(member);
            softly.assertThat(comment.getContent()).isEqualTo(content);
        });
    }

    @Test
    void Comment_객체의_content_를_수정한다() {
        // given
        Board board = mock(Board.class);
        Member member = mock(Member.class);

        Comment comment = Comment.builder()
                .member(member)
                .board(board)
                .content("수정 전 댓글")
                .build();
        String updatedComment = "수정 후 댓글";

        // when
        comment.updateContent(updatedComment);

        // then
        assertSoftly(softly -> {
            softly.assertThat(comment).isNotNull();
            softly.assertThat(comment.getContent()).isEqualTo(updatedComment);
            softly.assertThat(comment.getMember()).isEqualTo(member);
            softly.assertThat(comment.getBoard()).isEqualTo(board);
        });
    }
}
